package com.webcodein.hexagonal.domain;

import com.webcodein.hexagonal.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Invariant Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create valid money instance with non-negative amount")
    void shouldCreateValidMoney() {
        Money money = new Money(new BigDecimal("100.50"));
        assertEquals(new BigDecimal("100.50"), money.amount());
    }

    @Test
    @DisplayName("Should create valid money instance with zero amount")
    void shouldAllowZeroAmount() {
        Money money = new Money(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, money.amount());
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldRejectNegativeAmount() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-1.00"))
        );
        assertEquals("Amount cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldRejectNullAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Money(null)
        );
    }
}
