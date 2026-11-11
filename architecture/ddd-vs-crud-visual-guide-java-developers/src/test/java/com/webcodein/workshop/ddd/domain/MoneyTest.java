package com.webcodein.workshop.ddd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DDD Pattern: Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create valid Money instance")
    void shouldCreateMoney() {
        Money money = Money.of(BigDecimal.valueOf(199.99), "EUR");

        assertEquals(BigDecimal.valueOf(199.99), money.amount());
        assertEquals("EUR", money.currency());
    }

    @Test
    @DisplayName("Should reject negative amount")
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.valueOf(-1.0), "USD"));
    }

    @Test
    @DisplayName("Should reject invalid currency code")
    void shouldRejectInvalidCurrency() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.TEN, "TOOLONG"));
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.TEN, "US"));
    }

    @Test
    @DisplayName("Should add money with same currency")
    void shouldAddSameCurrency() {
        Money m1 = Money.usd(BigDecimal.valueOf(50.0));
        Money m2 = Money.usd(BigDecimal.valueOf(25.5));

        Money result = m1.add(m2);

        assertEquals(BigDecimal.valueOf(75.5), result.amount());
        assertEquals("USD", result.currency());
    }

    @Test
    @DisplayName("Should reject adding different currencies")
    void shouldRejectDifferentCurrencies() {
        Money usd = Money.usd(BigDecimal.valueOf(10.0));
        Money eur = Money.of(BigDecimal.valueOf(10.0), "EUR");

        assertThrows(IllegalArgumentException.class, () -> usd.add(eur));
    }
}
