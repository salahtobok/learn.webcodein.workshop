package com.webcodein.concurrency.model;

import java.math.BigDecimal;

public record OrderResult(
        String orderId,
        String status,
        String inventoryReservationId,
        String paymentTransactionId,
        int riskScore,
        long durationMs,
        String correlationId,
        String processedByThread
) {
}
