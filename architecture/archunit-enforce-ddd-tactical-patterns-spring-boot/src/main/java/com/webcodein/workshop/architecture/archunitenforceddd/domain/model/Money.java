package com.webcodein.workshop.architecture.archunitenforceddd.domain.model;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.ValueObject;
import java.math.BigDecimal;

@ValueObject
public final class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
