package com.promoquoter.promotion;

import com.promoquoter.domain.Promotion;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory to resolve which rule(s) apply to a promotion definition.
 */
@Component
public class PromotionRuleFactory {

    private final List<PromotionRule> rules;

    public PromotionRuleFactory(List<PromotionRule> rules) {
        this.rules = rules;
    }

    public PromotionRule getRuleFor(Promotion promotion) {
        return rules.stream()
                .filter(rule -> rule.supports(promotion))
                .findFirst()
                .orElse(null);
    }
}
