package com.promoquoter.dto;

import java.math.BigDecimal;

public class ConfirmResponse {

    private String orderId;
    private BigDecimal totalPrice;

    public ConfirmResponse() {
    }

    public ConfirmResponse(String orderId, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
