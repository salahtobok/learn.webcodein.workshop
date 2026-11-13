package com.webcodein.workshop.tls.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public record EarlyDataPacket(
        String ticketId,
        byte[] encryptedPayload,
        Instant clientTimestamp,
        String httpMethod,
        String path
) {
    public EarlyDataPacket {
        Objects.requireNonNull(ticketId, "ticketId must not be null");
        Objects.requireNonNull(encryptedPayload, "encryptedPayload must not be null");
        Objects.requireNonNull(clientTimestamp, "clientTimestamp must not be null");
        Objects.requireNonNull(httpMethod, "httpMethod must not be null");
        Objects.requireNonNull(path, "path must not be null");
        encryptedPayload = encryptedPayload.clone();
    }

    @Override
    public byte[] encryptedPayload() {
        return encryptedPayload.clone();
    }

    public boolean isSafeIdempotentMethod() {
        String method = httpMethod.toUpperCase();
        return "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EarlyDataPacket that)) return false;
        return ticketId.equals(that.ticketId) &&
                Arrays.equals(encryptedPayload, that.encryptedPayload) &&
                clientTimestamp.equals(that.clientTimestamp) &&
                httpMethod.equalsIgnoreCase(that.httpMethod) &&
                path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ticketId, clientTimestamp, httpMethod.toUpperCase(), path);
        result = 31 * result + Arrays.hashCode(encryptedPayload);
        return result;
    }
}
