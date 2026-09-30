package com.webcodein.cqrs.events;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID aggregateId,
        Instant timestamp
) implements DomainEvent {}
