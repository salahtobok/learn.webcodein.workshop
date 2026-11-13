package com.webcodein.workshop.protocol.model;

import java.util.Objects;

public record BenchmarkResult(
        ProtocolType protocol,
        int payloadSizeBytes,
        long serializationNanos,
        long simulatedNetworkLatencyNanos,
        long totalRoundTripNanos,
        int estimatedThroughputOpsPerSec
) {
    public BenchmarkResult {
        Objects.requireNonNull(protocol, "Protocol must not be null");
    }

    public static BenchmarkResult of(
            ProtocolType protocol,
            int payloadSizeBytes,
            long serializationNanos,
            long simulatedNetworkLatencyNanos
    ) {
        long total = serializationNanos + simulatedNetworkLatencyNanos;
        int throughput = (int) Math.min(2_000_000, 1_000_000_000L / Math.max(1, total));
        return new BenchmarkResult(protocol, payloadSizeBytes, serializationNanos, simulatedNetworkLatencyNanos, total, throughput);
    }
}
