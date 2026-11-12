package com.webcodein.workshop.ddd.domain.event;

import com.webcodein.workshop.ddd.domain.model.OrderId;

import java.time.Instant;
import java.util.Objects;

public record OrderShippedEvent(OrderId orderId, Instant occurredAt) {
    public OrderShippedEvent {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }

    public static OrderShippedEvent now(OrderId orderId) {
        return new OrderShippedEvent(orderId, Instant.now());
    }
}
