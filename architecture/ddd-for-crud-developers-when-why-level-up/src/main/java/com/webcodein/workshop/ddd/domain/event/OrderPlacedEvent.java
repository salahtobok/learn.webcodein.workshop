package com.webcodein.workshop.ddd.domain.event;

import com.webcodein.workshop.ddd.domain.model.Money;
import com.webcodein.workshop.ddd.domain.model.OrderId;

import java.time.Instant;
import java.util.Objects;

public record OrderPlacedEvent(OrderId orderId, Money totalAmount, Instant occurredAt) {
    public OrderPlacedEvent {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(totalAmount, "TotalAmount cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }

    public static OrderPlacedEvent now(OrderId orderId, Money totalAmount) {
        return new OrderPlacedEvent(orderId, totalAmount, Instant.now());
    }
}
