package com.promoquoter.promotion;

import com.promoquoter.domain.CustomerSegment;
import com.promoquoter.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Context for cart price calculation. Holds mutable line item state
 * for promotion rules to apply discounts. Supports audit trail.
 */
public class CartCalculationContext {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final CustomerSegment customerSegment;
    private final List<LineItemContext> lineItems = new ArrayList<>();
    private final List<String> appliedPromotionsAudit = new ArrayList<>();
    private BigDecimal cartLevelDiscount = BigDecimal.ZERO;

    public CartCalculationContext(CustomerSegment customerSegment) {
        this.customerSegment = customerSegment;
    }

    public void addLineItem(Product product, int quantity) {
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity))
                .setScale(SCALE, ROUNDING_MODE);
        lineItems.add(new LineItemContext(product, quantity, subtotal, BigDecimal.ZERO));
    }

    public CustomerSegment getCustomerSegment() {
        return customerSegment;
    }

    public List<LineItemContext> getLineItems() {
        return lineItems;
    }

    public List<String> getAppliedPromotionsAudit() {
        return appliedPromotionsAudit;
    }

    public void recordPromotion(String promotionDescription) {
        appliedPromotionsAudit.add(promotionDescription);
    }

    public void addCartLevelDiscount(BigDecimal amount) {
        this.cartLevelDiscount = this.cartLevelDiscount.add(amount).setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getCartLevelDiscount() {
        return cartLevelDiscount.setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getTotal() {
        BigDecimal itemsTotal = lineItems.stream()
                .map(LineItemContext::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, ROUNDING_MODE);
        return itemsTotal.subtract(cartLevelDiscount).max(BigDecimal.ZERO).setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getSubtotal() {
        return lineItems.stream()
                .map(LineItemContext::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getTotalDiscount() {
        BigDecimal lineDiscounts = lineItems.stream()
                .map(LineItemContext::getDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, ROUNDING_MODE);
        return lineDiscounts.add(cartLevelDiscount).setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Mutable line item for promotion rules to modify.
     */
    public static class LineItemContext {
        private final Product product;
        private final int quantity;
        private final BigDecimal subtotal;
        private BigDecimal discount;
        private final List<String> appliedPromotions = new ArrayList<>();

        public LineItemContext(Product product, int quantity, BigDecimal subtotal, BigDecimal discount) {
            this.product = product;
            this.quantity = quantity;
            this.subtotal = subtotal;
            this.discount = discount;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public BigDecimal getDiscount() {
            return discount;
        }

        public void addDiscount(BigDecimal amount) {
            this.discount = this.discount.add(amount).setScale(SCALE, ROUNDING_MODE);
        }

        public BigDecimal getFinalPrice() {
            return subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(SCALE, ROUNDING_MODE);
        }

        public List<String> getAppliedPromotions() {
            return appliedPromotions;
        }

        public void addAppliedPromotion(String promotion) {
            this.appliedPromotions.add(promotion);
        }
    }
}
