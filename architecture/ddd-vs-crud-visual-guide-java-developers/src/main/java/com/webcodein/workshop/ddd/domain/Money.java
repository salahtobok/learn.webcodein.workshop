package com.webcodein.workshop.ddd.domain;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a valid 3-letter code");
        }
    }
    
    // You can add business logic inside the Value Object!
    public Money add(Money other) {
        if (!this.currency.equals(other.currency())) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount()), this.currency);
    }
}
