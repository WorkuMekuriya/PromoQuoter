package com.promoquoter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "promotion_type")
    private PromotionType type;

    @Column(name = "category")
    private String category;

    @Column(name = "percentage_off", precision = 5, scale = 2)
    private BigDecimal percentageOff;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "buy_quantity")
    private Integer buyQuantity;

    @Column(name = "get_free_quantity")
    private Integer getFreeQuantity;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    @Column(name = "min_order_amount", precision = 19, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "waiver_amount", precision = 19, scale = 2)
    private BigDecimal waiverAmount;

    @Column(name = "priority", nullable = false)
    private int priority = 0;

    @Column(nullable = false)
    private boolean active = true;

    public Promotion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
