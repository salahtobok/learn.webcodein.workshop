package com.webcodein.taskmanager.presentation;

import com.webcodein.taskmanager.application.ProjectApplicationService;
import com.webcodein.taskmanager.application.dto.AddTaskCommand;
import com.webcodein.taskmanager.application.dto.CreateProjectCommand;
import com.webcodein.taskmanager.application.dto.ProjectDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectApplicationService appService;

    public ProjectController(ProjectApplicationService appService) {
        this.appService = appService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody CreateProjectCommand command) {
        UUID id = appService.createProject(command);
        return ResponseEntity.created(URI.create("/api/projects/" + id))
                .body(Map.of("id", id, "message", "Project created successfully"));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> listProjects() {
        return ResponseEntity.ok(appService.listProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(appService.getProject(id));
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Map<String, Object>> addTask(@PathVariable UUID id,
                                                        @RequestBody TaskRequest request) {
        UUID taskId = appService.addTaskToProject(id, new AddTaskCommand(request.title(), request.priorityLevel()));
        return ResponseEntity.created(URI.create("/api/projects/" + id + "/tasks/" + taskId))
                .body(Map.of("id", taskId, "projectId", id, "message", "Task added successfully"));
    }

    @PutMapping("/{id}/tasks/{taskId}/start")
    public ResponseEntity<Map<String, String>> startTask(@PathVariable UUID id,
                                                         @PathVariable UUID taskId) {
        appService.startTask(id, taskId);
        return ResponseEntity.ok(Map.of("message", "Task started"));
    }

    @PutMapping("/{id}/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, String>> completeTask(@PathVariable UUID id,
                                                            @PathVariable UUID taskId) {
        appService.markTaskDone(id, taskId);
        return ResponseEntity.ok(Map.of("message", "Task marked as done"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, String>> completeProject(@PathVariable UUID id) {
        appService.completeProject(id);
        return ResponseEntity.ok(Map.of("message", "Project completed"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID id) {
        appService.deleteProject(id);
    }

    public record TaskRequest(String title, Integer priorityLevel) {
        public TaskRequest(String title) {
            this(title, 2);
        }
    }
}
