package com.webcodein.workshop.quic.model;

/**
 * Performance metrics comparing TCP and QUIC delivery times under packet loss.
 */
public record BenchmarkResult(
    String protocol,
    int streamCount,
    double lossRatePercent,
    long totalBytesTransferred,
    long durationNanos,
    long holBlockedNanos,
    int retransmissions
) {
    public double durationMillis() {
        return durationNanos / 1_000_000.0;
    }

    public double holBlockedMillis() {
        return holBlockedNanos / 1_000_000.0;
    }

    public double throughputMegabytesPerSec() {
        if (durationNanos == 0) return 0.0;
        double seconds = durationNanos / 1_000_000_000.0;
        return (totalBytesTransferred / (1024.0 * 1024.0)) / seconds;
    }
}
