package com.webcodein.workshop.paseto.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable token claims representation according to standard RFC 7519 / PASETO specifications.
 */
public record TokenClaims(
    String subject,
    String issuer,
    String audience,
    Instant issuedAt,
    Instant expiresAt,
    Map<String, Object> customClaims
) {
    public TokenClaims {
        Objects.requireNonNull(subject, "Subject must not be null");
        Objects.requireNonNull(issuer, "Issuer must not be null");
        Objects.requireNonNull(issuedAt, "IssuedAt must not be null");
        Objects.requireNonNull(expiresAt, "ExpiresAt must not be null");
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("ExpiresAt cannot be earlier than IssuedAt");
        }
        customClaims = customClaims != null ? Collections.unmodifiableMap(Map.copyOf(customClaims)) : Map.of();
    }

    public boolean isExpired(Instant currentInstant) {
        return currentInstant.isAfter(expiresAt);
    }
}
