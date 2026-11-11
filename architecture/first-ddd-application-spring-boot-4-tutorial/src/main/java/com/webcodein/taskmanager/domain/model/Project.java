package com.webcodein.taskmanager.domain.model;

import com.webcodein.taskmanager.domain.event.ProjectCompletedEvent;
import com.webcodein.taskmanager.domain.event.TaskAddedEvent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    public static final int MAX_TASKS_PER_PROJECT = 50;

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private List<Task> tasks = new ArrayList<>();

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    protected Project() {
        // Required for JPA
    }

    public Project(String name) {
        this(UUID.randomUUID(), name);
    }

    public Project(UUID id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("Project id cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name is required and cannot be blank");
        }
        this.id = id;
        this.name = name.trim();
        this.status = ProjectStatus.ACTIVE;
    }

    public static Project reconstitute(UUID id, String name, ProjectStatus status, List<Task> tasks) {
        Project project = new Project();
        project.id = id;
        project.name = name;
        project.status = status;
        if (tasks != null) {
            project.tasks = new ArrayList<>(tasks);
        }
        return project;
    }

    public Task addTask(String title) {
        return addTask(title, Priority.MEDIUM);
    }

    public Task addTask(String title, Priority priority) {
        if (this.status != ProjectStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add tasks to non-active project (current status: " + this.status + ")");
        }
        if (this.tasks.size() >= MAX_TASKS_PER_PROJECT) {
            throw new IllegalStateException("Project reached maximum capacity of " + MAX_TASKS_PER_PROJECT + " tasks");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        String normalizedTitle = title.trim();
        boolean duplicate = this.tasks.stream()
                .anyMatch(t -> t.getTitle().equalsIgnoreCase(normalizedTitle));
        if (duplicate) {
            throw new IllegalArgumentException("Task with title '" + normalizedTitle + "' already exists in project");
        }

        UUID taskId = UUID.randomUUID();
        Task newTask = new Task(taskId, normalizedTitle, priority != null ? priority : Priority.MEDIUM);
        this.tasks.add(newTask);
        this.domainEvents.add(new TaskAddedEvent(this.id, taskId, normalizedTitle));
        return newTask;
    }

    public void complete() {
        if (this.status == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Project is already completed");
        }
        boolean hasPending = this.tasks.stream()
                .anyMatch(t -> t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.CANCELLED);
        if (hasPending) {
            throw new IllegalStateException("Cannot complete project with pending or in-progress tasks");
        }
        this.status = ProjectStatus.COMPLETED;
        this.domainEvents.add(new ProjectCompletedEvent(this.id, this.tasks.size()));
    }

    public void archive() {
        this.status = ProjectStatus.ARCHIVED;
    }

    public Optional<Task> findTask(UUID taskId) {
        return this.tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
