package com.webcodein.cqrs.events;

import java.time.Instant;
import java.util.UUID;

public sealed interface DomainEvent permits OrderCreatedEvent, OrderConfirmedEvent {
    UUID aggregateId();
    Instant timestamp();
}
