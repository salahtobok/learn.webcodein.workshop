package com.webcodein.workshop.quic.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable QUIC/HTTP frame representing a slice of data on an independent stream.
 */
public record StreamFrame(long streamId, long offset, byte[] payload, boolean fin) {

    public StreamFrame {
        Objects.requireNonNull(payload, "Payload must not be null");
        if (streamId < 0) {
            throw new IllegalArgumentException("Stream ID must be non-negative");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
        payload = payload.clone();
    }

    public int length() {
        return payload.length;
    }

    @Override
    public byte[] payload() {
        return payload.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamFrame that)) return false;
        return streamId == that.streamId &&
               offset == that.offset &&
               fin == that.fin &&
               Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(streamId, offset, fin);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
