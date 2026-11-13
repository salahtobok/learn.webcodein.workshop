package com.webcodein.workshop.httpclient.model;

import java.util.Objects;

public record AggregatedResponse(
        String serviceName,
        int statusCode,
        String responseBody,
        long latencyMs,
        boolean success
) {
    public AggregatedResponse {
        Objects.requireNonNull(serviceName, "serviceName must not be null");
    }

    public static AggregatedResponse ok(String serviceName, int statusCode, String responseBody, long latencyMs) {
        return new AggregatedResponse(serviceName, statusCode, responseBody, latencyMs, true);
    }

    public static AggregatedResponse failed(String serviceName, int statusCode, String errorMessage, long latencyMs) {
        return new AggregatedResponse(serviceName, statusCode, errorMessage, latencyMs, false);
    }
}
