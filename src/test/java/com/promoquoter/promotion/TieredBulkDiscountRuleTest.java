package com.promoquoter.promotion;

import com.promoquoter.domain.Product;
import com.promoquoter.domain.ProductCategory;
import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TieredBulkDiscountRuleTest {

    private final TieredBulkDiscountRule rule = new TieredBulkDiscountRule();

    @Test
    void apply_minQtyMet_shouldApplyDiscount() {
        Product product = new Product("Widget", ProductCategory.OTHER, new BigDecimal("10.00"), 100);
        product.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.TIERED_BULK_DISCOUNT);
        promotion.setProductId(1L);
        promotion.setMinQuantity(5);
        promotion.setPercentageOff(new BigDecimal("10"));

        CartCalculationContext context = new CartCalculationContext(com.promoquoter.domain.CustomerSegment.REGULAR);
        context.addLineItem(product, 5); // 5 * 10 = 50, 10% = 5 off

        rule.apply(promotion, context);

        assertEquals(new BigDecimal("50.00"), context.getSubtotal());
        assertEquals(new BigDecimal("5.00"), context.getTotalDiscount());
        assertEquals(new BigDecimal("45.00"), context.getTotal());
    }

    @Test
    void apply_minQtyNotMet_shouldNotApplyDiscount() {
        Product product = new Product("Widget", ProductCategory.OTHER, new BigDecimal("10.00"), 100);
        product.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.TIERED_BULK_DISCOUNT);
        promotion.setProductId(1L);
        promotion.setMinQuantity(5);
        promotion.setPercentageOff(new BigDecimal("10"));

        CartCalculationContext context = new CartCalculationContext(com.promoquoter.domain.CustomerSegment.REGULAR);
        context.addLineItem(product, 3);

        rule.apply(promotion, context);

        assertEquals(new BigDecimal("30.00"), context.getSubtotal());
        assertEquals(new BigDecimal("0.00"), context.getTotalDiscount());
        assertEquals(new BigDecimal("30.00"), context.getTotal());
    }

    @Test
    void supports_tieredBulk_returnsTrue() {
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.TIERED_BULK_DISCOUNT);
        promotion.setProductId(1L);
        promotion.setMinQuantity(5);
        promotion.setPercentageOff(new BigDecimal("10"));
        assertTrue(rule.supports(promotion));
    }
}
