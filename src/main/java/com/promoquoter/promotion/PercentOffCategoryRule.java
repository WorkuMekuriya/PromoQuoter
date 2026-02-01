package com.promoquoter.promotion;

import com.promoquoter.domain.ProductCategory;
import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy: Percentage discount for a product category.
 */
@Component
public class PercentOffCategoryRule implements PromotionRule {

    @Override
    public boolean supports(Promotion promotion) {
        return promotion.getType() == PromotionType.PERCENT_OFF_CATEGORY
                && promotion.getCategory() != null
                && promotion.getPercentageOff() != null
                && promotion.getPercentageOff().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public void apply(Promotion promotion, CartCalculationContext context) {
        String categoryStr = promotion.getCategory().toUpperCase();
        ProductCategory category;
        try {
            category = ProductCategory.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            return;
        }

        BigDecimal percentOff = promotion.getPercentageOff()
                .min(new BigDecimal("100"))
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        for (CartCalculationContext.LineItemContext item : context.getLineItems()) {
            if (item.getProduct().getCategory() == category) {
                BigDecimal discountAmount = item.getSubtotal().multiply(percentOff)
                        .setScale(CartCalculationContext.SCALE, CartCalculationContext.ROUNDING_MODE);
                item.addDiscount(discountAmount);
                item.addAppliedPromotion(
                        String.format("%s %s%% off", PromotionType.PERCENT_OFF_CATEGORY, promotion.getPercentageOff()));
            }
        }

        context.recordPromotion(
                String.format("%s: %s%% off %s", PromotionType.PERCENT_OFF_CATEGORY, promotion.getPercentageOff(), category));
    }
}
