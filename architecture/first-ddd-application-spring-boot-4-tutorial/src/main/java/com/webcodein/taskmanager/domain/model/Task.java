package com.webcodein.taskmanager.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Embedded
    private Priority priority;

    protected Task() {
        // Required for JPA
    }

    Task(UUID id, String title, Priority priority) {
        if (id == null) {
            throw new IllegalArgumentException("Task id cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        this.id = id;
        this.title = title.trim();
        this.status = TaskStatus.TODO;
        this.priority = priority != null ? priority : Priority.MEDIUM;
    }

    public static Task reconstitute(UUID id, String title, TaskStatus status, Priority priority) {
        Task task = new Task();
        task.id = id;
        task.title = title;
        task.status = status;
        task.priority = priority;
        return task;
    }

    public void startProgress() {
        if (this.status == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot start task that is already completed");
        }
        if (this.status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot start cancelled task");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void markAsDone() {
        if (this.status == TaskStatus.DONE) {
            throw new IllegalStateException("Task is already done");
        }
        if (this.status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete cancelled task");
        }
        this.status = TaskStatus.DONE;
    }

    public void cancel() {
        if (this.status == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot cancel completed task");
        }
        this.status = TaskStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
