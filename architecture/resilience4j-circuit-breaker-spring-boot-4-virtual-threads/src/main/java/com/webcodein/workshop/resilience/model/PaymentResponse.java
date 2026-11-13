package com.webcodein.workshop.resilience.model;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String transactionId,
        String status,
        BigDecimal amount,
        String currency,
        String message,
        boolean degradedFallback,
        Instant timestamp
) {
    public static PaymentResponse success(String transactionId, BigDecimal amount, String currency) {
        return new PaymentResponse(transactionId, "CONFIRMED", amount, currency, "Payment processed successfully", false, Instant.now());
    }

    public static PaymentResponse fallback(String transactionId, BigDecimal amount, String currency, String reason) {
        return new PaymentResponse(transactionId, "QUEUED_DEGRADED", amount, currency, "Gateway unavailable: " + reason, true, Instant.now());
    }
}
