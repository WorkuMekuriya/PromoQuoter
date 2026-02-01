package com.promoquoter.dto;

import com.promoquoter.domain.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "category is required")
    private ProductCategory category;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0", inclusive = true, message = "price must not be negative")
    private BigDecimal price;

    @Min(value = 0, message = "stock must not be negative")
    private int stock = 0;

    public ProductRequest() {
    }

    public ProductRequest(String name, ProductCategory category, BigDecimal price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
