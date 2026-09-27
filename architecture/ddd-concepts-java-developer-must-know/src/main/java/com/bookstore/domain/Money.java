package com.bookstore.domain;

import java.math.BigDecimal;
import java.util.Objects;

// Java Records are perfect for Value Objects
public record Money(BigDecimal amount, String currency) {
    
    // Compact constructor for validation
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Objects.requireNonNull(currency, "Currency is required");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(double multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }
    
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        if (this.amount.compareTo(other.amount) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
}
