package com.webcodein.workshop.fullstack.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record PriceUpdateEvent(
        String ticker,
        BigDecimal price,
        BigDecimal changePercent,
        long sequenceNumber,
        Instant timestamp
) {
    public PriceUpdateEvent {
        Objects.requireNonNull(ticker, "ticker must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(changePercent, "changePercent must not be null");
        if (ticker.isBlank()) {
            throw new IllegalArgumentException("ticker must not be blank");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("price must be positive");
        }
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException("sequenceNumber cannot be negative");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
