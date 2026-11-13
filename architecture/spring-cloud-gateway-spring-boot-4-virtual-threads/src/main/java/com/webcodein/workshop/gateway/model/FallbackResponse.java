package com.webcodein.workshop.gateway.model;

import java.time.Instant;
import java.util.Objects;

public record FallbackResponse(
        String routeId,
        String message,
        String status,
        boolean degraded,
        Instant timestamp
) {
    public FallbackResponse {
        Objects.requireNonNull(routeId, "routeId must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(status, "status must not be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
