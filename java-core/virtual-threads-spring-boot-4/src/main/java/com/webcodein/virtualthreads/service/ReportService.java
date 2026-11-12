package com.webcodein.virtualthreads.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final ReentrantLock unpinnedLock = new ReentrantLock();

    public Map<String, Object> generateSingleReport() {
        boolean isVirtual = Thread.currentThread().isVirtual();
        String threadInfo = Thread.currentThread().toString();
        log.info("Generating report on thread: {}, isVirtual: {}", threadInfo, isVirtual);

        try {
            // Simulates blocking network/database call where virtual thread yields continuation
            Thread.sleep(Duration.ofMillis(50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Report generation interrupted", e);
        }

        return Map.of(
            "status", "Success",
            "message", "Report generated successfully!",
            "isVirtual", isVirtual,
            "thread", threadInfo
        );
    }

    public List<Map<String, Object>> generateBatchReports(int count) {
        log.info("Dispatching batch of {} reports using Virtual Threads", count);
        List<Map<String, Object>> results = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Map<String, Object>>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                final int reportId = i + 1;
                futures.add(executor.submit(() -> {
                    Thread.sleep(Duration.ofMillis(30));
                    return Map.of(
                        "reportId", reportId,
                        "isVirtual", Thread.currentThread().isVirtual(),
                        "thread", Thread.currentThread().toString()
                    );
                }));
            }

            for (Future<Map<String, Object>> future : futures) {
                results.add(future.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch generation interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Subtask execution failed", e.getCause());
        }

        return results;
    }

    public Map<String, Object> executeWithReentrantLock() {
        unpinnedLock.lock();
        try {
            // ReentrantLock unpins carrier thread during blocking sleep unlike synchronized
            Thread.sleep(Duration.ofMillis(20));
            return Map.of(
                "status", "Success",
                "lockType", "ReentrantLock",
                "isVirtual", Thread.currentThread().isVirtual(),
                "pinned", false
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock execution interrupted", e);
        } finally {
            unpinnedLock.unlock();
        }
    }
}
