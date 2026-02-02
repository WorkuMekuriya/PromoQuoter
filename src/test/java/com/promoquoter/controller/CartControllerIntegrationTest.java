package com.promoquoter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promoquoter.domain.ProductCategory;
import com.promoquoter.dto.CartItemRequest;
import com.promoquoter.dto.CartRequest;
import com.promoquoter.dto.ProductRequest;
import com.promoquoter.domain.PromotionType;
import com.promoquoter.domain.CustomerSegment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long productId;

    @BeforeEach
    void setUp() throws Exception {
        ProductRequest productRequest = new ProductRequest("Test Product", ProductCategory.ELECTRONICS, new BigDecimal("100.00"), 100);
        String productResponse = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(productRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        productId = objectMapper.readTree(productResponse).get(0).get("id").asLong();
    }

    @Test
    void quote_shouldReturnItemizedBreakdown() throws Exception {
        CartRequest request = new CartRequest(
                List.of(new CartItemRequest(productId, 3)),
                CustomerSegment.REGULAR
        );
        mockMvc.perform(post("/cart/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.items[0].subtotal").value(300.00))
                .andExpect(jsonPath("$.total").value(300.00));
    }

    @Test
    void confirm_shouldReserveAndReturnOrderId() throws Exception {
        CartRequest request = new CartRequest(
                List.of(new CartItemRequest(productId, 2)),
                CustomerSegment.REGULAR
        );
        mockMvc.perform(post("/cart/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.totalPrice").value(200.00));
    }

    @Test
    void confirm_withIdempotencyKey_shouldReturnSameResult() throws Exception {
        CartRequest request = new CartRequest(
                List.of(new CartItemRequest(productId, 1)),
                CustomerSegment.REGULAR
        );
        String firstResponse = mockMvc.perform(post("/cart/confirm")
                        .header("Idempotency-Key", "test-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondResponse = mockMvc.perform(post("/cart/confirm")
                        .header("Idempotency-Key", "test-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String firstOrderId = objectMapper.readTree(firstResponse).get("orderId").asText();
        String secondOrderId = objectMapper.readTree(secondResponse).get("orderId").asText();
        assertEquals(firstOrderId, secondOrderId);
    }

    @Test
    void confirm_insufficientStock_shouldReturn409() throws Exception {
        CartRequest request = new CartRequest(
                List.of(new CartItemRequest(productId, 1000)),
                CustomerSegment.REGULAR
        );
        mockMvc.perform(post("/cart/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
