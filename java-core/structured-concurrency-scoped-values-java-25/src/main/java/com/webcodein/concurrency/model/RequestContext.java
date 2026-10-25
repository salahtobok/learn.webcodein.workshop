package com.webcodein.concurrency.model;

import java.time.Instant;
import java.util.UUID;

public record RequestContext(
        String correlationId,
        String userId,
        String tenantId,
        Instant timestamp
) {
    public static RequestContext anonymous() {
        return new RequestContext(
                UUID.randomUUID().toString(),
                "anonymous",
                "default",
                Instant.now()
        );
    }
}
