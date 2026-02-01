package com.promoquoter.promotion;

import com.promoquoter.domain.Promotion;

/**
 * Strategy interface for promotion rules. Each rule encapsulates its algorithm.
 */
public interface PromotionRule {

    /**
     * Returns true if this rule applies to the given promotion definition.
     */
    boolean supports(Promotion promotion);

    /**
     * Applies the promotion to the cart calculation context.
     * May modify context's line items and record to audit trail.
     */
    void apply(Promotion promotion, CartCalculationContext context);
}
