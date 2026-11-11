package com.webcodein.workshop.architecture.ddd.domain;

import com.webcodein.workshop.architecture.ddd.domain.model.order.*;
import com.webcodein.workshop.architecture.ddd.domain.service.PricingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DDD Domain Service: PricingService Tests")
class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    @DisplayName("Should apply 10% discount for VIP customer when order exceeds 100 USD")
    void shouldApplyDiscountForVipAboveThreshold() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("vip@example.com"));
        order.addLine(new BookId("BOOK-001"), Quantity.of(3), Money.of(50.00, "USD")); // total $150.00

        Money discount = pricingService.calculateDiscount(order, true);
        Money finalTotal = pricingService.calculateFinalTotal(order, true);

        assertThat(discount.amount()).isEqualTo(new BigDecimal("15.00"));
        assertThat(finalTotal.amount()).isEqualTo(new BigDecimal("135.00"));
    }

    @Test
    @DisplayName("Should not apply discount for VIP customer when order is 100 USD or less")
    void shouldNotApplyDiscountWhenUnderThreshold() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("vip@example.com"));
        order.addLine(new BookId("BOOK-001"), Quantity.of(2), Money.of(40.00, "USD")); // total $80.00

        Money discount = pricingService.calculateDiscount(order, true);
        Money finalTotal = pricingService.calculateFinalTotal(order, true);

        assertThat(discount.amount()).isEqualTo(new BigDecimal("0.00"));
        assertThat(finalTotal.amount()).isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    @DisplayName("Should not apply discount for non-VIP customer even above threshold")
    void shouldNotApplyDiscountForNonVip() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("regular@example.com"));
        order.addLine(new BookId("BOOK-001"), Quantity.of(4), Money.of(50.00, "USD")); // total $200.00

        Money discount = pricingService.calculateDiscount(order, false);
        Money finalTotal = pricingService.calculateFinalTotal(order, false);

        assertThat(discount.amount()).isEqualTo(new BigDecimal("0.00"));
        assertThat(finalTotal.amount()).isEqualTo(new BigDecimal("200.00"));
    }
}
