package com.webcodein.workshop.k8s.service;

import com.webcodein.workshop.k8s.model.WorkloadTask;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WorkloadProcessingService {

    private final AtomicInteger activeCount = new AtomicInteger(0);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final Map<String, WorkloadTask> taskRegistry = new ConcurrentHashMap<>();

    public WorkloadTask submitTask(String payload, int durationMs) {
        if (shuttingDown.get()) {
            throw new IllegalStateException("Node is currently draining in-flight requests and shutting down");
        }

        String id = UUID.randomUUID().toString();
        WorkloadTask task = new WorkloadTask(id, payload, durationMs, "PROCESSING");
        taskRegistry.put(id, task);
        activeCount.incrementAndGet();

        // Simulate async work completion
        Thread.ofVirtual().start(() -> {
            try {
                if (durationMs > 0) {
                    Thread.sleep(durationMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                taskRegistry.computeIfPresent(id, (k, v) -> v.withStatus("COMPLETED"));
                activeCount.decrementAndGet();
            }
        });

        return task;
    }

    public int getActiveCount() {
        return activeCount.get();
    }

    public boolean isShuttingDown() {
        return shuttingDown.get();
    }

    public void markShuttingDown() {
        this.shuttingDown.set(true);
    }

    public void resetShutdown() {
        this.shuttingDown.set(false);
    }

    public Collection<WorkloadTask> getAllTasks() {
        return Collections.unmodifiableCollection(taskRegistry.values());
    }

    public void completeTaskManually(String id) {
        WorkloadTask existing = taskRegistry.get(id);
        if (existing != null && "PROCESSING".equals(existing.status())) {
            taskRegistry.put(id, existing.withStatus("COMPLETED"));
            activeCount.decrementAndGet();
        }
    }
}
