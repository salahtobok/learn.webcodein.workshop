package com.webcodein.workshop.strategy.domain;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentType type,
        String customerId,
        String paymentDetails
) {}
