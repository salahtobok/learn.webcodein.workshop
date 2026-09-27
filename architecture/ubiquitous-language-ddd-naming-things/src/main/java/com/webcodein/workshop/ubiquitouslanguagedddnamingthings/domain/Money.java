package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import java.math.BigDecimal;
import java.util.Objects;

// GOOD - Uses a Value Object that encapsulates rules (Example 8)
public record Money(BigDecimal amount, String currencyCode) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currencyCode, "Currency code cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
    }
}
