package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Should create valid money value object")
    void createValidMoney() {
        Money money = new Money(BigDecimal.valueOf(15.00), "USD");
        assertEquals(BigDecimal.valueOf(15.00), money.amount());
        assertEquals("USD", money.currencyCode());
    }

    @Test
    @DisplayName("Should reject negative money amount")
    void rejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new Money(BigDecimal.valueOf(-1.00), "USD")
        );
    }

    @Test
    @DisplayName("Should reject null amount or currency code")
    void rejectNullFields() {
        assertThrows(NullPointerException.class, () -> new Money(null, "USD"));
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Should support value equality for identical money records")
    void moneyEquality() {
        Money m1 = new Money(BigDecimal.valueOf(10.50), "EUR");
        Money m2 = new Money(BigDecimal.valueOf(10.50), "EUR");
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }
}
