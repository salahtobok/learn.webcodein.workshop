package com.webcodein.taskmanager.application;

import com.webcodein.taskmanager.application.dto.AddTaskCommand;
import com.webcodein.taskmanager.application.dto.CreateProjectCommand;
import com.webcodein.taskmanager.application.dto.ProjectDto;
import com.webcodein.taskmanager.domain.model.Priority;
import com.webcodein.taskmanager.domain.model.Project;
import com.webcodein.taskmanager.domain.model.Task;
import com.webcodein.taskmanager.domain.repository.ProjectRepository;
import com.webcodein.taskmanager.domain.service.TaskAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class ProjectApplicationService {

    private final ProjectRepository projectRepository;
    private final TaskAssignmentService taskAssignmentService;

    public ProjectApplicationService(ProjectRepository projectRepository,
                                     TaskAssignmentService taskAssignmentService) {
        this.projectRepository = projectRepository;
        this.taskAssignmentService = taskAssignmentService;
    }

    public UUID createProject(CreateProjectCommand command) {
        Project project = new Project(command.name());
        Project saved = projectRepository.save(project);
        return saved.getId();
    }

    public UUID addTaskToProject(UUID projectId, String title) {
        Task task = taskAssignmentService.assignTask(projectId, title);
        return task.getId();
    }

    public UUID addTaskToProject(UUID projectId, AddTaskCommand command) {
        Priority priority = command.priorityLevel() != null
                ? Priority.of(command.priorityLevel())
                : Priority.MEDIUM;
        Task task = taskAssignmentService.assignTask(projectId, command.title(), priority);
        return task.getId();
    }

    public void startTask(UUID projectId, UUID taskId) {
        Project project = loadProject(projectId);
        Task task = project.findTask(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));
        task.startProgress();
        projectRepository.save(project);
    }

    public void markTaskDone(UUID projectId, UUID taskId) {
        Project project = loadProject(projectId);
        Task task = project.findTask(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));
        task.markAsDone();
        projectRepository.save(project);
    }

    public void completeProject(UUID projectId) {
        Project project = loadProject(projectId);
        project.complete();
        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public ProjectDto getProject(UUID projectId) {
        Project project = loadProject(projectId);
        return ProjectDto.fromDomain(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> listProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectDto::fromDomain)
                .toList();
    }

    public void deleteProject(UUID projectId) {
        projectRepository.deleteById(projectId);
    }

    private Project loadProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
    }
}
