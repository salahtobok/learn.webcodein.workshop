package com.webcodein.taskmanager.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ProjectCompletedEvent(
        UUID projectId,
        int totalTasks,
        Instant completedAt
) {
    public ProjectCompletedEvent(UUID projectId, int totalTasks) {
        this(projectId, totalTasks, Instant.now());
    }
}
