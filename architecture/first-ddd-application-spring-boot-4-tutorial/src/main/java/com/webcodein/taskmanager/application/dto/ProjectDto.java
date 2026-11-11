package com.webcodein.taskmanager.application.dto;

import com.webcodein.taskmanager.domain.model.Project;

import java.util.List;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String name,
        String status,
        List<TaskDto> tasks
) {
    public static ProjectDto fromDomain(Project project) {
        List<TaskDto> taskDtos = project.getTasks().stream()
                .map(TaskDto::fromDomain)
                .toList();
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getStatus().name(),
                taskDtos
        );
    }
}
