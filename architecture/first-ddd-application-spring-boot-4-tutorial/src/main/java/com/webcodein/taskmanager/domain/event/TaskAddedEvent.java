package com.webcodein.taskmanager.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TaskAddedEvent(
        UUID projectId,
        UUID taskId,
        String taskTitle,
        Instant occurredOn
) {
    public TaskAddedEvent(UUID projectId, UUID taskId, String taskTitle) {
        this(projectId, taskId, taskTitle, Instant.now());
    }
}
