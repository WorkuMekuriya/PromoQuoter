package com.promoquoter.controller;

import com.promoquoter.domain.Product;
import com.promoquoter.dto.ProductRequest;
import com.promoquoter.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProducts(@Valid @RequestBody List<ProductRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.badRequest().body("At least one product is required");
        }
        List<Product> products = productService.createProducts(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                products.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getCategory().name(), p.getPrice(), p.getStock());
    }

    public record ProductResponse(Long id, String name, String category, java.math.BigDecimal price, int stock) {
    }
}
