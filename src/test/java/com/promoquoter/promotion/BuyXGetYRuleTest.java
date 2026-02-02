package com.promoquoter.promotion;

import com.promoquoter.domain.Product;
import com.promoquoter.domain.ProductCategory;
import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuyXGetYRuleTest {

    private final BuyXGetYRule rule = new BuyXGetYRule();

    @Test
    void apply_buy2Get1Free_shouldGiveCorrectDiscount() {
        Product product = new Product("Widget", ProductCategory.OTHER, new BigDecimal("10.00"), 100);
        product.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.BUY_X_GET_Y);
        promotion.setProductId(1L);
        promotion.setBuyQuantity(2);
        promotion.setGetFreeQuantity(1);

        CartCalculationContext context = new CartCalculationContext(com.promoquoter.domain.CustomerSegment.REGULAR);
        context.addLineItem(product, 3); // 3 items = 1 free

        rule.apply(promotion, context);

        assertEquals(new BigDecimal("30.00"), context.getSubtotal());
        assertEquals(new BigDecimal("10.00"), context.getTotalDiscount());
        assertEquals(new BigDecimal("20.00"), context.getTotal());
    }

    @Test
    void supports_buyXGetY_returnsTrue() {
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.BUY_X_GET_Y);
        promotion.setProductId(1L);
        promotion.setBuyQuantity(2);
        promotion.setGetFreeQuantity(1);
        org.junit.jupiter.api.Assertions.assertTrue(rule.supports(promotion));
    }
}
