package com.webcodein.workshop.k8s.model;

import java.util.Objects;

public record WorkloadTask(
        String id,
        String payload,
        int processingDurationMs,
        String status
) {
    public WorkloadTask {
        Objects.requireNonNull(id, "Task id cannot be null");
        Objects.requireNonNull(payload, "Payload cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Task id cannot be blank");
        }
        if (processingDurationMs < 0) {
            throw new IllegalArgumentException("Processing duration cannot be negative");
        }
    }

    public WorkloadTask withStatus(String newStatus) {
        return new WorkloadTask(id, payload, processingDurationMs, newStatus);
    }
}
