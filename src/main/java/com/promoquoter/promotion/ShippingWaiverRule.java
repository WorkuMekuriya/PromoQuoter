package com.promoquoter.promotion;

import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy: Shipping waiver - flat discount when cart subtotal meets minimum.
 * E.g., "Free shipping on orders over $50" (waiverAmount = shipping cost).
 */
@Component
public class ShippingWaiverRule implements PromotionRule {

    @Override
    public boolean supports(Promotion promotion) {
        return promotion.getType() == PromotionType.SHIPPING_WAIVER
                && promotion.getMinOrderAmount() != null
                && promotion.getMinOrderAmount().compareTo(BigDecimal.ZERO) >= 0
                && promotion.getWaiverAmount() != null
                && promotion.getWaiverAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public void apply(Promotion promotion, CartCalculationContext context) {
        BigDecimal minOrder = promotion.getMinOrderAmount();
        BigDecimal waiverAmount = promotion.getWaiverAmount();

        BigDecimal currentSubtotal = context.getSubtotal();
        BigDecimal currentTotalAfterLineDiscounts = context.getLineItems().stream()
                .map(CartCalculationContext.LineItemContext::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CartCalculationContext.SCALE, CartCalculationContext.ROUNDING_MODE);

        if (currentSubtotal.compareTo(minOrder) >= 0) {
            BigDecimal applicableWaiver = waiverAmount.min(currentTotalAfterLineDiscounts);
            context.addCartLevelDiscount(applicableWaiver);
            context.recordPromotion(
                    String.format("%s: order >= %s, waiver %s", PromotionType.SHIPPING_WAIVER, minOrder, waiverAmount));
        }
    }
}
