package com.webcodein.workshop.kafka.model;

import java.math.BigDecimal;
import java.util.Objects;

public record OrderEvent(
        String orderId,
        String customerId,
        BigDecimal amount,
        String eventType,
        int retryCount
) {
    public OrderEvent {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        if (orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be blank");
        }
        if (customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount cannot be negative");
        }
    }

    public OrderEvent withIncrementedRetry() {
        return new OrderEvent(orderId, customerId, amount, eventType, retryCount + 1);
    }
}
