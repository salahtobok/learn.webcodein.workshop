package com.webcodein.workshop.architecture.ddd.domain.event;

import com.webcodein.workshop.architecture.ddd.domain.model.order.CustomerEmail;
import com.webcodein.workshop.architecture.ddd.domain.model.order.OrderId;

import java.time.Instant;
import java.util.Objects;

public record OrderPlacedEvent(OrderId orderId, CustomerEmail customerEmail, Instant occurredAt) {
    public OrderPlacedEvent {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(customerEmail, "CustomerEmail cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }
}
