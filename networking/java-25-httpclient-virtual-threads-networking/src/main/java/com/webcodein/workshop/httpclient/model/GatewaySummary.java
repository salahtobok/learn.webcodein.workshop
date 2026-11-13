package com.webcodein.workshop.httpclient.model;

import java.util.List;
import java.util.Objects;

public record GatewaySummary(
        List<AggregatedResponse> results,
        long totalWallClockTimeMs,
        boolean allSucceeded
) {
    public GatewaySummary {
        Objects.requireNonNull(results, "results must not be null");
    }
}
