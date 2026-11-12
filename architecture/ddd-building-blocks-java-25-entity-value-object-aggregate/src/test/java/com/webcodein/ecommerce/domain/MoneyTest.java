package com.webcodein.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Should create Money with positive amount and currency")
    void shouldCreateMoney() {
        Money money = Money.of(new BigDecimal("100.50"), "USD");

        assertThat(money.amount()).isEqualTo(new BigDecimal("100.50"));
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Money records with identical attributes should be equal")
    void shouldBeEqualForIdenticalAttributes() {
        Money m1 = Money.of(new BigDecimal("50.00"), "EUR");
        Money m2 = Money.of(new BigDecimal("50.00"), "EUR");

        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }

    @Test
    @DisplayName("Negative amount should throw IllegalArgumentException")
    void shouldThrowWhenAmountIsNegative() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be negative");
    }

    @Test
    @DisplayName("Null amount or currency should throw NullPointerException")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> Money.of(null, "USD"))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> Money.of(BigDecimal.TEN, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Add with same currency should produce new Money with summed amount")
    void shouldAddSameCurrency() {
        Money m1 = Money.of(new BigDecimal("20.00"), "USD");
        Money m2 = Money.of(new BigDecimal("30.00"), "USD");

        Money sum = m1.add(m2);

        assertThat(sum.amount()).isEqualTo(new BigDecimal("50.00"));
        assertThat(sum.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Add with different currencies should throw IllegalArgumentException")
    void shouldThrowWhenAddingDifferentCurrencies() {
        Money usd = Money.of(new BigDecimal("20.00"), "USD");
        Money eur = Money.of(new BigDecimal("30.00"), "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot add different currencies");
    }

    @Test
    @DisplayName("Multiply should scale amount by factor")
    void shouldMultiplyAmount() {
        Money m = Money.of(new BigDecimal("15.00"), "USD");
        Money result = m.multiply(3);

        assertThat(result.amount()).isEqualTo(new BigDecimal("45.00"));
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Multiply by negative factor should throw IllegalArgumentException")
    void shouldThrowWhenMultiplyByNegative() {
        Money m = Money.of(new BigDecimal("15.00"), "USD");

        assertThatThrownBy(() -> m.multiply(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Factor cannot be negative");
    }
}
