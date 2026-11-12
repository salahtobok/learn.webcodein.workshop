package com.webcodein.workshop.architecture.archunitenforceddd.domain.model;

import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.ValueObject;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Money {

    private final BigDecimal amount;
    private final String currency;

    protected Money() {
        this.amount = BigDecimal.ZERO;
        this.currency = "USD";
    }

    public Money(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.currency = Objects.requireNonNull(currency, "Currency must not be null");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
