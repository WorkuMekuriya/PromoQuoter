package com.promoquoter.service;

import com.promoquoter.domain.Order;
import com.promoquoter.domain.Product;
import com.promoquoter.dto.CartItemRequest;
import com.promoquoter.dto.CartRequest;
import com.promoquoter.dto.ConfirmResponse;
import com.promoquoter.dto.LineItemDto;
import com.promoquoter.dto.QuoteResponse;
import com.promoquoter.exception.InsufficientStockException;
import com.promoquoter.exception.ProductNotFoundException;
import com.promoquoter.promotion.CartCalculationContext;
import com.promoquoter.promotion.PromotionPipeline;
import com.promoquoter.repository.OrderRepository;
import com.promoquoter.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PromotionPipeline promotionPipeline;
    private final IdempotencyService idempotencyService;

    public CartService(ProductRepository productRepository,
                       OrderRepository orderRepository,
                       PromotionPipeline promotionPipeline,
                       IdempotencyService idempotencyService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.promotionPipeline = promotionPipeline;
        this.idempotencyService = idempotencyService;
    }

    @Transactional(readOnly = true)
    public QuoteResponse getQuote(CartRequest request) {
        Map<Long, Integer> productQuantities = request.getItems().stream()
                .collect(Collectors.toMap(CartItemRequest::getProductId, CartItemRequest::getQty, Integer::sum));

        List<Long> productIds = new ArrayList<>(productQuantities.keySet());
        List<Product> products = productRepository.findByIdIn(productIds);

        if (products.size() != productIds.size()) {
            List<Long> foundIds = products.stream().map(Product::getId).toList();
            List<Long> missing = productIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new ProductNotFoundException("Products not found: " + missing);
        }

        CartCalculationContext context = new CartCalculationContext(request.getCustomerSegment());

        for (Product product : products) {
            int qty = productQuantities.get(product.getId());
            context.addLineItem(product, qty);
        }

        promotionPipeline.apply(context);

        return buildQuoteResponse(context);
    }

    @Transactional
    public ConfirmResponse confirm(CartRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            return orderRepository.findByIdempotencyKey(idempotencyKey)
                    .map(order -> new ConfirmResponse(order.getOrderId(), order.getTotalPrice()))
                    .orElseGet(() -> executeConfirm(request, idempotencyKey));
        }
        return executeConfirm(request, null);
    }

    private ConfirmResponse executeConfirm(CartRequest request, String idempotencyKey) {
        Map<Long, Integer> productQuantities = request.getItems().stream()
                .collect(Collectors.toMap(CartItemRequest::getProductId, CartItemRequest::getQty, Integer::sum));

        List<Long> productIds = new ArrayList<>(productQuantities.keySet());

        List<Product> products = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
            products.add(product);
        }

        for (Product product : products) {
            int requested = productQuantities.get(product.getId());
            if (product.getStock() < requested) {
                throw new InsufficientStockException(
                        "Insufficient stock for product " + product.getId() + ": required " + requested + ", available " + product.getStock());
            }
        }

        CartCalculationContext context = new CartCalculationContext(request.getCustomerSegment());
        for (Product product : products) {
            int qty = productQuantities.get(product.getId());
            context.addLineItem(product, qty);
        }
        promotionPipeline.apply(context);

        BigDecimal total = context.getTotal();

        for (Product product : products) {
            int requested = productQuantities.get(product.getId());
            product.setStock(product.getStock() - requested);
            productRepository.save(product);
        }

        String orderId = Order.generateOrderId();
        String key = idempotencyKey != null ? idempotencyKey : "gen-" + UUID.randomUUID();
        Order order = new Order(orderId, total, key);
        try {
            orderRepository.save(order);
            return new ConfirmResponse(orderId, total);
        } catch (DataIntegrityViolationException e) {
            if (idempotencyKey != null) {
                return idempotencyService.findByKey(idempotencyKey)
                        .map(o -> new ConfirmResponse(o.getOrderId(), o.getTotalPrice()))
                        .orElseThrow(() -> e);
            }
            throw e;
        }
    }

    private QuoteResponse buildQuoteResponse(CartCalculationContext context) {
        QuoteResponse response = new QuoteResponse();
        response.setSubtotal(context.getSubtotal());
        response.setTotalDiscount(context.getTotalDiscount());
        response.setTotal(context.getTotal());
        response.setAppliedPromotionsInOrder(new ArrayList<>(context.getAppliedPromotionsAudit()));

        for (CartCalculationContext.LineItemContext item : context.getLineItems()) {
            LineItemDto dto = new LineItemDto();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getProduct().getPrice());
            dto.setSubtotal(item.getSubtotal());
            dto.setDiscount(item.getDiscount());
            dto.setFinalPrice(item.getFinalPrice());
            dto.setAppliedPromotions(String.join("; ", item.getAppliedPromotions()));
            response.getItems().add(dto);
        }

        return response;
    }
}
