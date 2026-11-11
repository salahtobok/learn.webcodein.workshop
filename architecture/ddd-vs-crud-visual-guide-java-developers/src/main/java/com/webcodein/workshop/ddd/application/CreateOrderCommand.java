package com.webcodein.workshop.ddd.application;

import java.math.BigDecimal;

public record CreateOrderCommand(String email, BigDecimal amount, String currency) {
    public CreateOrderCommand(String email, BigDecimal amount) {
        this(email, amount, "USD");
    }
}
