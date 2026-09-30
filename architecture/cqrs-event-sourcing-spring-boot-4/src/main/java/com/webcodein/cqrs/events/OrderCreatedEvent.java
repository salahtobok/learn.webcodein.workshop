package com.webcodein.cqrs.events;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID aggregateId,
        String customerId,
        double amount,
        Instant timestamp
) implements DomainEvent {}
