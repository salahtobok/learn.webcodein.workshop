package com.webcodein.workshop.k8s.model;

import java.util.Objects;

public record PodLifecycleState(
        String podName,
        String readinessStatus,
        String livenessStatus,
        int inFlightRequests,
        boolean shuttingDown
) {
    public PodLifecycleState {
        Objects.requireNonNull(podName, "Pod name cannot be null");
        Objects.requireNonNull(readinessStatus, "Readiness status cannot be null");
        Objects.requireNonNull(livenessStatus, "Liveness status cannot be null");
        if (inFlightRequests < 0) {
            throw new IllegalArgumentException("In-flight requests count cannot be negative");
        }
    }
}
