package com.bookstore.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    @DisplayName("VIP member receives 20% discount on book price")
    void shouldApplyVipDiscount() {
        BookstoreMember vip = new BookstoreMember(new MemberId("vip-1"), true);
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "DDD Guide", new Money(new BigDecimal("100.00"), "USD"));

        Money finalPrice = pricingService.calculateFinalPrice(vip, book);

        assertThat(finalPrice.amount()).isEqualByComparingTo("80.00");
        assertThat(finalPrice.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Clearance book receives flat $5 discount for non-VIP members")
    void shouldApplyClearanceDiscount() {
        BookstoreMember regular = new BookstoreMember(new MemberId("reg-1"), false);
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "Old Edition", new Money(new BigDecimal("30.00"), "USD"), true, 50, 2);

        Money finalPrice = pricingService.calculateFinalPrice(regular, book);

        assertThat(finalPrice.amount()).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("Regular member receives base price for non-clearance book")
    void shouldReturnBasePrice() {
        BookstoreMember regular = new BookstoreMember(new MemberId("reg-1"), false);
        BookAggregate book = new BookAggregate(UUID.randomUUID(), "New Release", new Money(new BigDecimal("45.00"), "USD"), false, 100, 10);

        Money finalPrice = pricingService.calculateFinalPrice(regular, book);

        assertThat(finalPrice.amount()).isEqualByComparingTo("45.00");
    }
}
