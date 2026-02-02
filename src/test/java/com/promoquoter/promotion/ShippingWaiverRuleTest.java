package com.promoquoter.promotion;

import com.promoquoter.domain.Product;
import com.promoquoter.domain.ProductCategory;
import com.promoquoter.domain.Promotion;
import com.promoquoter.domain.PromotionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShippingWaiverRuleTest {

    private final ShippingWaiverRule rule = new ShippingWaiverRule();

    @Test
    void apply_orderMeetsMinimum_shouldApplyWaiver() {
        Product product = new Product("Laptop", ProductCategory.ELECTRONICS, new BigDecimal("100.00"), 10);
        product.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.SHIPPING_WAIVER);
        promotion.setMinOrderAmount(new BigDecimal("50"));
        promotion.setWaiverAmount(new BigDecimal("5.99")); // shipping cost waived

        CartCalculationContext context = new CartCalculationContext(com.promoquoter.domain.CustomerSegment.REGULAR);
        context.addLineItem(product, 1); // subtotal 100 >= 50

        rule.apply(promotion, context);

        assertEquals(new BigDecimal("100.00"), context.getSubtotal());
        assertEquals(new BigDecimal("5.99"), context.getCartLevelDiscount());
        assertEquals(new BigDecimal("94.01"), context.getTotal());
    }

    @Test
    void apply_orderBelowMinimum_shouldNotApplyWaiver() {
        Product product = new Product("Cable", ProductCategory.ELECTRONICS, new BigDecimal("10.00"), 100);
        product.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.SHIPPING_WAIVER);
        promotion.setMinOrderAmount(new BigDecimal("50"));
        promotion.setWaiverAmount(new BigDecimal("5.99"));

        CartCalculationContext context = new CartCalculationContext(com.promoquoter.domain.CustomerSegment.REGULAR);
        context.addLineItem(product, 2); // subtotal 20 < 50

        rule.apply(promotion, context);

        assertEquals(new BigDecimal("20.00"), context.getSubtotal());
        assertEquals(new BigDecimal("0.00"), context.getCartLevelDiscount());
        assertEquals(new BigDecimal("20.00"), context.getTotal());
    }

    @Test
    void supports_shippingWaiver_returnsTrue() {
        Promotion promotion = new Promotion();
        promotion.setType(PromotionType.SHIPPING_WAIVER);
        promotion.setMinOrderAmount(new BigDecimal("50"));
        promotion.setWaiverAmount(new BigDecimal("5.99"));
        assertTrue(rule.supports(promotion));
    }
}
