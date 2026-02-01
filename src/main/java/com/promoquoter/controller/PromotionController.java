package com.promoquoter.controller;

import com.promoquoter.domain.Promotion;
import com.promoquoter.dto.PromotionRequest;
import com.promoquoter.service.PromotionService;
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
@RequestMapping("/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping
    public ResponseEntity<?> createPromotions(@Valid @RequestBody List<PromotionRequest> requests) {
        if (requests.isEmpty()) {
            return ResponseEntity.badRequest().body("At least one promotion is required");
        }
        List<Promotion> promotions = promotionService.createPromotions(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                promotions.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    private PromotionResponse toResponse(Promotion p) {
        return new PromotionResponse(
                p.getId(),
                p.getType().name(),
                p.getCategory(),
                p.getPercentageOff(),
                p.getProductId(),
                p.getBuyQuantity(),
                p.getGetFreeQuantity(),
                p.getMinQuantity(),
                p.getMinOrderAmount(),
                p.getWaiverAmount(),
                p.getPriority(),
                p.isActive()
        );
    }

    public record PromotionResponse(Long id, String type, String category,
                                    java.math.BigDecimal percentageOff,
                                    Long productId, Integer buyQuantity, Integer getFreeQuantity,
                                    Integer minQuantity, java.math.BigDecimal minOrderAmount, java.math.BigDecimal waiverAmount,
                                    int priority, boolean active) {
    }
}
