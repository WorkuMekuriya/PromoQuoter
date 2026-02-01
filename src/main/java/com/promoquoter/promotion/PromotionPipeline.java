package com.promoquoter.promotion;

import com.promoquoter.domain.Promotion;
import com.promoquoter.repository.PromotionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Chain of Responsibility / Pipeline: applies promotions in defined order
 * (by priority). Orchestration code is independent of concrete rules.
 */
@Component
public class PromotionPipeline {

    private final PromotionRepository promotionRepository;
    private final PromotionRuleFactory ruleFactory;

    public PromotionPipeline(PromotionRepository promotionRepository, PromotionRuleFactory ruleFactory) {
        this.promotionRepository = promotionRepository;
        this.ruleFactory = ruleFactory;
    }

    /**
     * Applies all active promotions in priority order to the context.
     * Rules can be swapped, added, or removed without changing this orchestration.
     */
    public void apply(CartCalculationContext context) {
        List<Promotion> promotions = promotionRepository.findByActiveTrueOrderByPriorityAsc();

        for (Promotion promotion : promotions) {
            PromotionRule rule = ruleFactory.getRuleFor(promotion);
            if (rule != null) {
                rule.apply(promotion, context);
            }
        }
    }
}
