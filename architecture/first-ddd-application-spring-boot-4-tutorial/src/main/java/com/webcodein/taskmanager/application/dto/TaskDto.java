package com.webcodein.taskmanager.application.dto;

import com.webcodein.taskmanager.domain.model.Task;

import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String status,
        int priorityLevel,
        String priorityName
) {
    public static TaskDto fromDomain(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getStatus().name(),
                task.getPriority() != null ? task.getPriority().level() : 2,
                task.getPriority() != null ? task.getPriority().name() : "MEDIUM"
        );
    }
}
