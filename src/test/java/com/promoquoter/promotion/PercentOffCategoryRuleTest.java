package com.promoquoter.promotion;

import com.promoquoter.domain.Product;
import com.promoquoter.domain.ProductCategory;
import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PercentOffCategoryRuleTest {

    private final PercentOffCategoryRule rule = new PercentOffCategoryRule();

    @Test
    void apply_shouldApplyPercentageDiscount() {
        Product product = new Product("Laptop", ProductCategory.ELECTRONICS, new BigDecimal("100.00"), 10);
        product.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);
        promotion.setCategory("ELECTRONICS");
        promotion.setPercentageOff(new BigDecimal("10"));

        CartCalculationContext context = new CartCalculationContext(com.promoquoter.domain.CustomerSegment.REGULAR);
        context.addLineItem(product, 2);

        rule.apply(promotion, context);

        assertEquals(new BigDecimal("200.00"), context.getSubtotal());
        assertEquals(new BigDecimal("20.00"), context.getTotalDiscount());
        assertEquals(new BigDecimal("180.00"), context.getTotal());
    }

    @Test
    void supports_percentOffCategory_returnsTrue() {
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.PERCENT_OFF_CATEGORY);
        promotion.setCategory("ELECTRONICS");
        promotion.setPercentageOff(new BigDecimal("10"));
        org.junit.jupiter.api.Assertions.assertTrue(rule.supports(promotion));
    }

    @Test
    void supports_buyXGetY_returnsFalse() {
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.BUY_X_GET_Y);
        org.junit.jupiter.api.Assertions.assertFalse(rule.supports(promotion));
    }
}
