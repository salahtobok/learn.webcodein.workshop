package com.webcodein.workshop.gateway.model;

import java.util.Objects;

public record RouteDefinition(
        String id,
        String uri,
        String pathPattern,
        int rateLimitRps,
        boolean requiresAuth
) {
    public RouteDefinition {
        Objects.requireNonNull(id, "Route ID must not be null");
        Objects.requireNonNull(uri, "Route URI must not be null");
        Objects.requireNonNull(pathPattern, "Path pattern must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Route ID must not be blank");
        }
        if (uri.isBlank() || (!uri.startsWith("http://") && !uri.startsWith("https://"))) {
            throw new IllegalArgumentException("Route URI must start with http:// or https://");
        }
        if (pathPattern.isBlank() || !pathPattern.startsWith("/")) {
            throw new IllegalArgumentException("Path pattern must start with /");
        }
        if (rateLimitRps <= 0) {
            throw new IllegalArgumentException("Rate limit RPS must be positive");
        }
    }
}
