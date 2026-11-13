package com.webcodein.workshop.realtime.model;

import java.util.Objects;

public record ProtocolMetrics(
        String protocol,
        int totalFrames,
        long totalBytes,
        double avgLatencyMs,
        double framingOverheadRatio
) {
    public ProtocolMetrics {
        Objects.requireNonNull(protocol, "protocol must not be null");
        if (totalFrames < 0 || totalBytes < 0) {
            throw new IllegalArgumentException("Counts must not be negative");
        }
    }
}
