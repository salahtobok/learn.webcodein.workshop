package com.webcodein.taskmanager.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Entity Tests")
class TaskTest {

    @Test
    @DisplayName("Should create task with TODO status")
    void shouldCreateTaskWithTodoStatus() {
        Task task = new Task(UUID.randomUUID(), "Write documentation", Priority.MEDIUM);

        assertEquals("Write documentation", task.getTitle());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(Priority.MEDIUM, task.getPriority());
    }

    @Test
    @DisplayName("Should transition from TODO to IN_PROGRESS")
    void shouldTransitionToInProgress() {
        Task task = new Task(UUID.randomUUID(), "Write tests", Priority.HIGH);
        task.startProgress();

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    @DisplayName("Should transition from IN_PROGRESS to DONE")
    void shouldTransitionToDone() {
        Task task = new Task(UUID.randomUUID(), "Fix bug", Priority.URGENT);
        task.startProgress();
        task.markAsDone();

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    @DisplayName("Should reject starting already completed task")
    void shouldRejectStartingCompletedTask() {
        Task task = new Task(UUID.randomUUID(), "Deploy", Priority.HIGH);
        task.markAsDone();

        IllegalStateException ex = assertThrows(IllegalStateException.class, task::startProgress);
        assertTrue(ex.getMessage().contains("already completed"));
    }

    @Test
    @DisplayName("Should cancel task")
    void shouldCancelTask() {
        Task task = new Task(UUID.randomUUID(), "Deprecated feature", Priority.LOW);
        task.cancel();

        assertEquals(TaskStatus.CANCELLED, task.getStatus());
    }

    @Test
    @DisplayName("Should reject cancelling already completed task")
    void shouldRejectCancellingCompletedTask() {
        Task task = new Task(UUID.randomUUID(), "Release v1", Priority.HIGH);
        task.markAsDone();

        IllegalStateException ex = assertThrows(IllegalStateException.class, task::cancel);
        assertTrue(ex.getMessage().contains("Cannot cancel completed task"));
    }

    @Test
    @DisplayName("Should reconstitute task with arbitrary state")
    void shouldReconstituteTask() {
        UUID id = UUID.randomUUID();
        Task task = Task.reconstitute(id, "Historic Task", TaskStatus.DONE, Priority.LOW);

        assertEquals(id, task.getId());
        assertEquals("Historic Task", task.getTitle());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals(Priority.LOW, task.getPriority());
    }
}
