package com.webcodein.workshop.k8s;

import com.webcodein.workshop.k8s.model.WorkloadTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadTaskTest {

    @Test
    @DisplayName("Valid task creation should preserve fields")
    void validTaskCreation() {
        WorkloadTask task = new WorkloadTask("task-1", "order-event", 50, "QUEUED");
        assertEquals("task-1", task.id());
        assertEquals("order-event", task.payload());
        assertEquals(50, task.processingDurationMs());
        assertEquals("QUEUED", task.status());
    }

    @Test
    @DisplayName("Null ID should throw NullPointerException")
    void nullIdThrows() {
        assertThrows(NullPointerException.class, () ->
                new WorkloadTask(null, "order-event", 50, "QUEUED"));
    }

    @Test
    @DisplayName("Blank ID should throw IllegalArgumentException")
    void blankIdThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkloadTask("   ", "order-event", 50, "QUEUED"));
    }

    @Test
    @DisplayName("Negative duration should throw IllegalArgumentException")
    void negativeDurationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkloadTask("task-1", "order-event", -10, "QUEUED"));
    }

    @Test
    @DisplayName("withStatus should return a new record with updated status")
    void withStatusUpdatesCorrectly() {
        WorkloadTask task = new WorkloadTask("task-1", "order-event", 50, "PROCESSING");
        WorkloadTask updated = task.withStatus("COMPLETED");
        assertEquals("COMPLETED", updated.status());
        assertEquals(task.id(), updated.id());
        assertEquals(task.payload(), updated.payload());
    }
}
