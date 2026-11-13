package com.webcodein.workshop.httpclient.model;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public record DownstreamTarget(
        String serviceName,
        URI targetUri,
        Duration timeout
) {
    public DownstreamTarget {
        Objects.requireNonNull(serviceName, "serviceName must not be null");
        Objects.requireNonNull(targetUri, "targetUri must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
    }

    public static DownstreamTarget of(String serviceName, String uriString, Duration timeout) {
        return new DownstreamTarget(serviceName, URI.create(uriString), timeout);
    }
}
