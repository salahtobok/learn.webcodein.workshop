package com.webcodein.hikaricp.dto;

public record PoolMetricsResponse(
        String poolName,
        int activeConnections,
        int idleConnections,
        int totalConnections,
        int threadsAwaitingConnection,
        int maximumPoolSize,
        int minimumIdle
) {
}
