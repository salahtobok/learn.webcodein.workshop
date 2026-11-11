package com.bookstore.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Should successfully instantiate Money with valid positive amount")
    void shouldCreateValidMoney() {
        Money money = new Money(new BigDecimal("49.99"), "USD");
        assertThat(money.amount()).isEqualByComparingTo("49.99");
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should reject negative amount in compact constructor")
    void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> new Money(new BigDecimal("-10.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be negative");
    }

    @Test
    @DisplayName("Should reject null currency")
    void shouldRejectNullCurrency() {
        assertThatThrownBy(() -> new Money(new BigDecimal("10.00"), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Currency is required");
    }

    @Test
    @DisplayName("Should add money with matching currencies")
    void shouldAddMoney() {
        Money a = new Money(new BigDecimal("20.00"), "USD");
        Money b = new Money(new BigDecimal("15.50"), "USD");

        Money sum = a.add(b);

        assertThat(sum.amount()).isEqualByComparingTo("35.50");
        assertThat(sum.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw exception when adding mismatching currencies")
    void shouldThrowOnCurrencyMismatchAdd() {
        Money usd = new Money(new BigDecimal("20.00"), "USD");
        Money eur = new Money(new BigDecimal("20.00"), "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currency mismatch");
    }

    @Test
    @DisplayName("Should multiply money by scale factor")
    void shouldMultiplyMoney() {
        Money base = new Money(new BigDecimal("100.00"), "USD");
        Money discounted = base.multiply(0.8);

        assertThat(discounted.amount()).isEqualByComparingTo("80.00");
    }

    @Test
    @DisplayName("Should subtract money and throw if result is negative")
    void shouldSubtractMoneyAndCheckNegative() {
        Money high = new Money(new BigDecimal("100.00"), "USD");
        Money low = new Money(new BigDecimal("40.00"), "USD");

        Money diff = high.subtract(low);
        assertThat(diff.amount()).isEqualByComparingTo("60.00");

        assertThatThrownBy(() -> low.subtract(high))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Result cannot be negative");
    }

    @Test
    @DisplayName("Value objects must provide structural equality")
    void shouldProvideStructuralEquality() {
        Money first = new Money(new BigDecimal("25.00"), "USD");
        Money second = new Money(new BigDecimal("25.00"), "USD");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }
}
