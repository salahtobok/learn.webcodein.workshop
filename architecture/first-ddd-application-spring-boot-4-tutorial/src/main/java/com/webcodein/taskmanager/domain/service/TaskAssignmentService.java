package com.webcodein.taskmanager.domain.service;

import com.webcodein.taskmanager.domain.model.Priority;
import com.webcodein.taskmanager.domain.model.Project;
import com.webcodein.taskmanager.domain.model.Task;
import com.webcodein.taskmanager.domain.repository.ProjectRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

public class TaskAssignmentService {

    private final ProjectRepository projectRepository;

    public TaskAssignmentService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Task assignTask(UUID projectId, String taskTitle) {
        return assignTask(projectId, taskTitle, Priority.MEDIUM);
    }

    public Task assignTask(UUID projectId, String taskTitle, Priority priority) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));

        Task task = project.addTask(taskTitle, priority);
        projectRepository.save(project);
        return task;
    }
}
