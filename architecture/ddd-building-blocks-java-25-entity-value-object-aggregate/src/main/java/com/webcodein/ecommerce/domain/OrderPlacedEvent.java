package com.webcodein.ecommerce.domain;

import java.util.UUID;
import java.time.Instant;

// The name must be in the past tense (OrderPlaced, CustomerRegistered)
public record OrderPlacedEvent(
    UUID eventId, 
    Instant occurredOn, 
    UUID orderId, 
    UUID customerId, 
    String totalAmountStr
) {
    // Convenience constructor for creating new events
    public OrderPlacedEvent(UUID orderId, UUID customerId, String totalAmountStr) {
        this(UUID.randomUUID(), Instant.now(), orderId, customerId, totalAmountStr);
    }
}
