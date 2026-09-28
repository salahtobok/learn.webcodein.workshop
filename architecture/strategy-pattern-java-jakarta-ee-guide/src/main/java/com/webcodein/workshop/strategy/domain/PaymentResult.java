package com.webcodein.workshop.strategy.domain;

public record PaymentResult(
        boolean success,
        String transactionId,
        String message
) {}
