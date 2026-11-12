package com.webcodein.workshop.ddd.domain;

import com.webcodein.workshop.ddd.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Should create valid money with 2 decimal places")
    void validMoneyCreation() {
        Money money = Money.of(BigDecimal.valueOf(19.99), "USD");
        assertEquals(BigDecimal.valueOf(19.99).setScale(2), money.amount());
        assertEquals("USD", money.currency());
    }

    @Test
    @DisplayName("Should reject negative money amount")
    void rejectNegativeMoney() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                Money.of(BigDecimal.valueOf(-5.00), "USD")
        );
        assertTrue(ex.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Should reject invalid currency code")
    void rejectInvalidCurrency() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.TEN, ""));
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.TEN, "US"));
        assertThrows(IllegalArgumentException.class, () -> Money.of(BigDecimal.TEN, "USDD"));
    }

    @Test
    @DisplayName("Should add money with matching currency")
    void addMatchingCurrency() {
        Money m1 = Money.of(15.50, "EUR");
        Money m2 = Money.of(4.50, "EUR");
        Money sum = m1.add(m2);
        assertEquals(Money.of(20.00, "EUR"), sum);
    }

    @Test
    @DisplayName("Should reject adding money with currency mismatch")
    void rejectCurrencyMismatchOnAdd() {
        Money usd = Money.of(10.00, "USD");
        Money eur = Money.of(10.00, "EUR");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usd.add(eur));
        assertTrue(ex.getMessage().contains("Currency mismatch"));
    }

    @Test
    @DisplayName("Should subtract money and reject negative result")
    void subtractMoney() {
        Money m1 = Money.of(25.00, "USD");
        Money m2 = Money.of(10.00, "USD");
        assertEquals(Money.of(15.00, "USD"), m1.subtract(m2));

        assertThrows(IllegalArgumentException.class, () -> m2.subtract(m1));
    }
}
