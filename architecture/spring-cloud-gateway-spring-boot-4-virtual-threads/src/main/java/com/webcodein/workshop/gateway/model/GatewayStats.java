package com.webcodein.workshop.gateway.model;

import java.util.List;

public record GatewayStats(
        String status,
        int activeRouteCount,
        long totalForwardedRequests,
        boolean virtualThreadsEnabled,
        List<RouteDefinition> routes
) {
    public GatewayStats {
        if (activeRouteCount < 0) {
            throw new IllegalArgumentException("Active route count cannot be negative");
        }
        if (totalForwardedRequests < 0) {
            throw new IllegalArgumentException("Total forwarded requests cannot be negative");
        }
        routes = routes == null ? List.of() : List.copyOf(routes);
    }
}
