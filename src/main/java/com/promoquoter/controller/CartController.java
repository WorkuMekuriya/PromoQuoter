package com.promoquoter.controller;

import com.promoquoter.dto.CartRequest;
import com.promoquoter.dto.ConfirmResponse;
import com.promoquoter.dto.QuoteResponse;
import com.promoquoter.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/quote")
    public ResponseEntity<QuoteResponse> getQuote(@Valid @RequestBody CartRequest request) {
        QuoteResponse response = cartService.getQuote(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmResponse> confirm(
            @Valid @RequestBody CartRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        ConfirmResponse response = cartService.confirm(request, idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
