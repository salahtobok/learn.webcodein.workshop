package com.webcodein.workshop.fullstack.model;

import java.time.Instant;
import java.util.Objects;

public record ClientSubscription(
        String clientId,
        String tickerFilter,
        Instant subscribedAt
) {
    public ClientSubscription {
        Objects.requireNonNull(clientId, "clientId must not be null");
        if (clientId.isBlank()) {
            throw new IllegalArgumentException("clientId must not be blank");
        }
        if (subscribedAt == null) {
            subscribedAt = Instant.now();
        }
    }
}
