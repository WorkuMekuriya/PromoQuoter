package com.promoquoter.dto;

import com.promoquoter.domain.PromotionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PromotionRequest {

    @NotNull(message = "type is required")
    private PromotionType type;

    private String category;

    @DecimalMin(value = "0", inclusive = true, message = "percentageOff must not be negative")
    private BigDecimal percentageOff;

    private Long productId;

    @Min(value = 1, message = "buyQuantity must be at least 1")
    private Integer buyQuantity;

    @Min(value = 0, message = "getFreeQuantity must not be negative")
    private Integer getFreeQuantity;

    @Min(value = 1, message = "minQuantity must be at least 1 when provided")
    private Integer minQuantity;

    @DecimalMin(value = "0", inclusive = true, message = "minOrderAmount must not be negative")
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0", inclusive = true, message = "waiverAmount must not be negative")
    private BigDecimal waiverAmount;

    @Min(value = 0, message = "priority must not be negative")
    private int priority = 0;

    public PromotionRequest() {
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPercentageOff() {
        return percentageOff;
    }

    public void setPercentageOff(BigDecimal percentageOff) {
        this.percentageOff = percentageOff;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(Integer buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public Integer getGetFreeQuantity() {
        return getFreeQuantity;
    }

    public void setGetFreeQuantity(Integer getFreeQuantity) {
        this.getFreeQuantity = getFreeQuantity;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getWaiverAmount() {
        return waiverAmount;
    }

    public void setWaiverAmount(BigDecimal waiverAmount) {
        this.waiverAmount = waiverAmount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
