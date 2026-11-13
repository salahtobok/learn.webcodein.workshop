package com.webcodein.workshop.tls.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public record SessionTicket(
        String ticketId,
        byte[] resumptionMasterSecret,
        Instant issuedAt,
        Duration lifetime,
        int maxEarlyDataSize,
        CipherSuite cipherSuite
) {
    public SessionTicket {
        Objects.requireNonNull(ticketId, "ticketId must not be null");
        Objects.requireNonNull(resumptionMasterSecret, "resumptionMasterSecret must not be null");
        Objects.requireNonNull(issuedAt, "issuedAt must not be null");
        Objects.requireNonNull(lifetime, "lifetime must not be null");
        Objects.requireNonNull(cipherSuite, "cipherSuite must not be null");
        if (maxEarlyDataSize < 0) {
            throw new IllegalArgumentException("maxEarlyDataSize cannot be negative");
        }
        resumptionMasterSecret = resumptionMasterSecret.clone();
    }

    @Override
    public byte[] resumptionMasterSecret() {
        return resumptionMasterSecret.clone();
    }

    public boolean isExpired(Instant currentInstant) {
        return currentInstant.isAfter(issuedAt.plus(lifetime));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionTicket that)) return false;
        return maxEarlyDataSize == that.maxEarlyDataSize &&
                ticketId.equals(that.ticketId) &&
                Arrays.equals(resumptionMasterSecret, that.resumptionMasterSecret) &&
                issuedAt.equals(that.issuedAt) &&
                lifetime.equals(that.lifetime) &&
                cipherSuite == that.cipherSuite;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ticketId, issuedAt, lifetime, maxEarlyDataSize, cipherSuite);
        result = 31 * result + Arrays.hashCode(resumptionMasterSecret);
        return result;
    }
}
