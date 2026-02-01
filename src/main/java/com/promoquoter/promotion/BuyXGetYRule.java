package com.promoquoter.promotion;

import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy: Buy X get Y free for a given product.
 */
@Component
public class BuyXGetYRule implements PromotionRule {

    @Override
    public boolean supports(Promotion promotion) {
        return promotion.getType() == PromotionType.BUY_X_GET_Y
                && promotion.getProductId() != null
                && promotion.getBuyQuantity() != null
                && promotion.getBuyQuantity() > 0
                && promotion.getGetFreeQuantity() != null
                && promotion.getGetFreeQuantity() > 0;
    }

    @Override
    public void apply(Promotion promotion, CartCalculationContext context) {
        Long productId = promotion.getProductId();
        int buyQty = promotion.getBuyQuantity();
        int getFreeQty = promotion.getGetFreeQuantity();

        for (CartCalculationContext.LineItemContext item : context.getLineItems()) {
            if (item.getProduct().getId().equals(productId)) {
                int quantity = item.getQuantity();
                int freeSets = quantity / (buyQty + getFreeQty);
                int freeUnits = freeSets * getFreeQty;

                if (freeUnits > 0) {
                    BigDecimal unitPrice = item.getProduct().getPrice();
                    BigDecimal discountAmount = unitPrice.multiply(BigDecimal.valueOf(freeUnits))
                            .setScale(CartCalculationContext.SCALE, CartCalculationContext.ROUNDING_MODE);
                    item.addDiscount(discountAmount);
                    item.addAppliedPromotion(
                            String.format("%s: Buy %d Get %d free", PromotionType.BUY_X_GET_Y, buyQty, getFreeQty));
                }
            }
        }

        context.recordPromotion(
                String.format("%s: product %d, buy %d get %d free", PromotionType.BUY_X_GET_Y, productId, buyQty, getFreeQty));
    }
}
