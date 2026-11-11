package com.webcodein.workshop.architecture.ddd.domain.event;

import com.webcodein.workshop.architecture.ddd.domain.model.order.Money;
import com.webcodein.workshop.architecture.ddd.domain.model.order.OrderId;

import java.time.Instant;
import java.util.Objects;

public record OrderConfirmedEvent(OrderId orderId, Money totalAmount, Instant occurredAt) {
    public OrderConfirmedEvent {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        Objects.requireNonNull(totalAmount, "TotalAmount cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }
}
