package com.webcodein.workshop.architecture.archunitenforceddd;

import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Invariant Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create valid money instance")
    void shouldCreateValidMoney() {
        Money money = new Money(new BigDecimal("99.95"), "USD");
        assertEquals(new BigDecimal("99.95"), money.getAmount());
        assertEquals("USD", money.getCurrency());
    }

    @Test
    @DisplayName("Should reject negative money amounts")
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new Money(new BigDecimal("-5.00"), "USD"));
    }

    @Test
    @DisplayName("Should reject null currency")
    void shouldRejectNullCurrency() {
        assertThrows(NullPointerException.class, () ->
                new Money(new BigDecimal("10.00"), null));
    }

    @Test
    @DisplayName("Should respect equality semantics based on values")
    void shouldRespectEquality() {
        Money m1 = new Money(new BigDecimal("100.00"), "EUR");
        Money m2 = new Money(new BigDecimal("100.00"), "EUR");
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }
}
