package com.webcodein.workshop.k8s;

import com.webcodein.workshop.k8s.model.WorkloadTask;
import com.webcodein.workshop.k8s.service.WorkloadProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadProcessingServiceTest {

    private WorkloadProcessingService service;

    @BeforeEach
    void setUp() {
        service = new WorkloadProcessingService();
    }

    @Test
    @DisplayName("Submitting task should record task in registry and increment active count")
    void submitTaskRecordsProperly() {
        WorkloadTask task = service.submitTask("test-payload", 1000);
        assertNotNull(task.id());
        assertEquals("test-payload", task.payload());
        assertEquals("PROCESSING", task.status());
        assertTrue(service.getActiveCount() >= 1);
    }

    @Test
    @DisplayName("Shutdown flag should reject subsequent tasks with IllegalStateException")
    void shutdownRejectsNewTasks() {
        service.markShuttingDown();
        assertTrue(service.isShuttingDown());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.submitTask("fail-payload", 50));
        assertTrue(ex.getMessage().contains("draining in-flight requests"));
    }

    @Test
    @DisplayName("Resetting shutdown state should allow task submission again")
    void resetShutdownRestoresSubmission() {
        service.markShuttingDown();
        service.resetShutdown();
        assertFalse(service.isShuttingDown());

        WorkloadTask task = service.submitTask("restored-payload", 100);
        assertNotNull(task);
    }

    @Test
    @DisplayName("Manual task completion should decrement active count and update status")
    void manualCompletionUpdatesCount() {
        WorkloadTask task = service.submitTask("manual-payload", 5000);
        service.completeTaskManually(task.id());
        WorkloadTask updated = service.getAllTasks().stream()
                .filter(t -> t.id().equals(task.id()))
                .findFirst()
                .orElseThrow();
        assertEquals("COMPLETED", updated.status());
    }
}
