package com.webcodein.cicd.model;

import java.time.Instant;
import java.util.Objects;

public record DeploymentStatus(
        String status,
        String version,
        String hostname,
        String message,
        Instant timestamp
) {
    public DeploymentStatus {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(version, "version must not be null");
        Objects.requireNonNull(hostname, "hostname must not be null");
        Objects.requireNonNull(message, "message must not be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public static DeploymentStatus up(String version, String hostname) {
        return new DeploymentStatus("UP", version, hostname, "Zero-downtime deployment successful!", Instant.now());
    }
}
