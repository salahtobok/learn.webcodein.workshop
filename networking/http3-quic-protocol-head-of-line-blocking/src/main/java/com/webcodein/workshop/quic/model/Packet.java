package com.webcodein.workshop.quic.model;

import java.util.List;
import java.util.Objects;

/**
 * A network packet carrying one or more stream frames over an underlying transport.
 */
public record Packet(long packetNumber, List<StreamFrame> frames, long sentTimestampNanos) {

    public Packet {
        Objects.requireNonNull(frames, "Frames must not be null");
        if (packetNumber < 0) {
            throw new IllegalArgumentException("Packet number must be non-negative");
        }
        frames = List.copyOf(frames);
    }

    public boolean containsStream(long streamId) {
        return frames.stream().anyMatch(f -> f.streamId() == streamId);
    }
}
