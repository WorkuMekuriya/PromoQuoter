package com.promoquoter.promotion;

import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy: Tiered bulk discount - percentage off when quantity reaches threshold.
 * E.g., "Buy 5+ get 5% off, Buy 10+ get 10% off" (one tier per promotion).
 */
@Component
public class TieredBulkDiscountRule implements PromotionRule {

    @Override
    public boolean supports(Promotion promotion) {
        return promotion.getType() == PromotionType.TIERED_BULK_DISCOUNT
                && promotion.getProductId() != null
                && promotion.getMinQuantity() != null
                && promotion.getMinQuantity() > 0
                && promotion.getPercentageOff() != null
                && promotion.getPercentageOff().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public void apply(Promotion promotion, CartCalculationContext context) {
        Long productId = promotion.getProductId();
        int minQty = promotion.getMinQuantity();
        BigDecimal percentOff = promotion.getPercentageOff()
                .min(new BigDecimal("100"))
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        for (CartCalculationContext.LineItemContext item : context.getLineItems()) {
            if (item.getProduct().getId().equals(productId) && item.getQuantity() >= minQty) {
                BigDecimal discountAmount = item.getSubtotal().multiply(percentOff)
                        .setScale(CartCalculationContext.SCALE, CartCalculationContext.ROUNDING_MODE);
                item.addDiscount(discountAmount);
                item.addAppliedPromotion(
                        String.format("%s: %d+ units = %s%% off", PromotionType.TIERED_BULK_DISCOUNT, minQty, promotion.getPercentageOff()));
            }
        }

        context.recordPromotion(
                String.format("%s: product %d, min qty %d, %s%% off", PromotionType.TIERED_BULK_DISCOUNT, productId, minQty, promotion.getPercentageOff()));
    }
}
