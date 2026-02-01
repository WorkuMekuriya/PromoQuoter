package com.promoquoter.dto;

import com.promoquoter.domain.CustomerSegment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CartRequest {

    @NotEmpty(message = "items list cannot be empty")
    @Valid
    private List<CartItemRequest> items;

    @NotNull(message = "customerSegment is required")
    private CustomerSegment customerSegment = CustomerSegment.REGULAR;

    public CartRequest() {
    }

    public CartRequest(List<CartItemRequest> items, CustomerSegment customerSegment) {
        this.items = items;
        this.customerSegment = customerSegment;
    }

    public List<CartItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }

    public CustomerSegment getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(CustomerSegment customerSegment) {
        this.customerSegment = customerSegment;
    }
}
