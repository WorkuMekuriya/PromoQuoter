package com.promoquoter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promoquoter.domain.PromotionType;
import com.promoquoter.dto.PromotionRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPromotions_percentOff_shouldReturn201() throws Exception {
        PromotionRequest request = new PromotionRequest();
        request.setType(PromotionType.PERCENT_OFF_CATEGORY);
        request.setCategory("ELECTRONICS");
        request.setPercentageOff(new BigDecimal("10"));
        request.setPriority(0);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].type").value("PERCENT_OFF_CATEGORY"))
                .andExpect(jsonPath("$[0].category").value("ELECTRONICS"))
                .andExpect(jsonPath("$[0].percentageOff").value(10));
    }

    @Test
    void createPromotions_buyXGetY_shouldReturn201() throws Exception {
        PromotionRequest request = new PromotionRequest();
        request.setType(PromotionType.BUY_X_GET_Y);
        request.setProductId(1L);
        request.setBuyQuantity(2);
        request.setGetFreeQuantity(1);
        request.setPriority(1);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].type").value("BUY_X_GET_Y"))
                .andExpect(jsonPath("$[0].buyQuantity").value(2))
                .andExpect(jsonPath("$[0].getFreeQuantity").value(1));
    }

    @Test
    void createPromotions_tieredBulk_shouldReturn201() throws Exception {
        PromotionRequest request = new PromotionRequest();
        request.setType(PromotionType.TIERED_BULK_DISCOUNT);
        request.setProductId(1L);
        request.setMinQuantity(5);
        request.setPercentageOff(new BigDecimal("10"));
        request.setPriority(2);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].type").value("TIERED_BULK_DISCOUNT"))
                .andExpect(jsonPath("$[0].minQuantity").value(5));
    }

    @Test
    void createPromotions_shippingWaiver_shouldReturn201() throws Exception {
        PromotionRequest request = new PromotionRequest();
        request.setType(PromotionType.SHIPPING_WAIVER);
        request.setMinOrderAmount(new BigDecimal("50"));
        request.setWaiverAmount(new BigDecimal("5.99"));
        request.setPriority(3);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].type").value("SHIPPING_WAIVER"))
                .andExpect(jsonPath("$[0].minOrderAmount").value(50))
                .andExpect(jsonPath("$[0].waiverAmount").value(5.99));
    }
}
