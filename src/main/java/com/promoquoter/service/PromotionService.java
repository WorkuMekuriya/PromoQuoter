package com.promoquoter.service;

import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import com.promoquoter.dto.PromotionRequest;
import com.promoquoter.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Transactional
    public Promotion createPromotion(PromotionRequest request) {
        Promotion promotion = new Promotion();
        promotion.setType(request.getType());
        promotion.setCategory(request.getCategory());
        promotion.setPercentageOff(request.getPercentageOff());
        promotion.setProductId(request.getProductId());
        promotion.setBuyQuantity(request.getBuyQuantity());
        promotion.setGetFreeQuantity(request.getGetFreeQuantity());
        promotion.setMinQuantity(request.getMinQuantity());
        promotion.setMinOrderAmount(request.getMinOrderAmount());
        promotion.setWaiverAmount(request.getWaiverAmount());
        promotion.setPriority(request.getPriority());
        promotion.setActive(true);
        return promotionRepository.save(promotion);
    }

    @Transactional
    public List<Promotion> createPromotions(List<PromotionRequest> requests) {
        return requests.stream()
                .map(this::createPromotion)
                .collect(Collectors.toList());
    }
}
