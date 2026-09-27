package com.webcodein.ecommerce.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currencyCode) {
    
    // Compact constructor for validation
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currencyCode, "Currency code cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException("Currency code must be exactly 3 characters");
        }
    }
    
    // Factory method for convenience
    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), currencyCode);
    }
    
    // Operations return a NEW Value Object, preserving immutability
    public Money add(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currencyCode);
    }
    
    public Money subtract(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot subtract different currencies");
        }
        return new Money(this.amount.subtract(other.amount), this.currencyCode);
    }
}
