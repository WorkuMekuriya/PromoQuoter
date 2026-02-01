package com.promoquoter.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QuoteResponse {

    private List<LineItemDto> items = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private List<String> appliedPromotionsInOrder = new ArrayList<>();

    public QuoteResponse() {
    }

    public List<LineItemDto> getItems() {
        return items;
    }

    public void setItems(List<LineItemDto> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<String> getAppliedPromotionsInOrder() {
        return appliedPromotionsInOrder;
    }

    public void setAppliedPromotionsInOrder(List<String> appliedPromotionsInOrder) {
        this.appliedPromotionsInOrder = appliedPromotionsInOrder;
    }
}
