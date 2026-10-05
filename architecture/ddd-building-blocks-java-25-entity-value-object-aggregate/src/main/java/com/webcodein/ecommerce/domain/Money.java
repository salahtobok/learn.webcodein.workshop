package com.webcodein.ecommerce.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public record Money(BigDecimal amount, String currency) implements Serializable {
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
}
