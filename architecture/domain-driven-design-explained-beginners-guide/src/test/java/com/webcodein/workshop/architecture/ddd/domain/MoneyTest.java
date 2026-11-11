package com.webcodein.workshop.architecture.ddd.domain;

import com.webcodein.workshop.architecture.ddd.domain.model.order.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DDD Value Object: Money Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create valid Money with half-up 2 decimal scale")
    void shouldCreateValidMoney() {
        Money money = Money.of(29.994, "USD");
        assertThat(money.amount()).isEqualTo(new BigDecimal("29.99"));
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should reject negative money amount")
    void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> new Money(new BigDecimal("-5.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    @DisplayName("Should reject invalid currency code")
    void shouldRejectInvalidCurrencyCode() {
        assertThatThrownBy(() -> new Money(new BigDecimal("10.00"), "US"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("3-letter ISO code");
    }

    @Test
    @DisplayName("Should add money with matching currency")
    void shouldAddMoneyWithMatchingCurrency() {
        Money m1 = Money.of(15.50, "USD");
        Money m2 = Money.of(24.50, "USD");

        Money sum = m1.add(m2);
        assertThat(sum.amount()).isEqualTo(new BigDecimal("40.00"));
        assertThat(sum.currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should reject adding money with different currencies")
    void shouldRejectAddingDifferentCurrencies() {
        Money usd = Money.of(10.00, "USD");
        Money eur = Money.of(10.00, "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different currencies");
    }

    @Test
    @DisplayName("Should multiply money by integer and BigDecimal factors")
    void shouldMultiplyMoney() {
        Money unitPrice = Money.of(19.99, "USD");

        Money total = unitPrice.multiply(3);
        assertThat(total.amount()).isEqualTo(new BigDecimal("59.97"));

        Money discounted = total.multiply(new BigDecimal("0.10"));
        assertThat(discounted.amount()).isEqualTo(new BigDecimal("6.00"));
    }

    @Test
    @DisplayName("Should compare money values accurately")
    void shouldCompareMoneyValues() {
        Money low = Money.of(50.00, "USD");
        Money high = Money.of(100.00, "USD");

        assertThat(high.isGreaterThan(low)).isTrue();
        assertThat(low.isGreaterThan(high)).isFalse();
    }
}
