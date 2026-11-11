package com.webcodein.workshop.ddd.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (currency == null || currency.trim().length() != 3) {
            throw new IllegalArgumentException("Currency must be a valid 3-letter ISO code");
        }
        currency = currency.trim().toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money usd(BigDecimal amount) {
        return new Money(amount, "USD");
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null money");
        if (!this.currency.equals(other.currency())) {
            throw new IllegalArgumentException("Cannot add different currencies: " + this.currency + " and " + other.currency());
        }
        return new Money(this.amount.add(other.amount()), this.currency);
    }
}
