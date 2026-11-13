package com.webcodein.workshop.security.model;

import java.time.Instant;
import java.util.Objects;

public record SecurityAuditRecord(
        String id,
        String subject,
        String actor,
        String audience,
        String scopesGranted,
        Instant timestamp
) {
    public SecurityAuditRecord {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Objects.requireNonNull(audience, "audience must not be null");
        Objects.requireNonNull(scopesGranted, "scopesGranted must not be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
