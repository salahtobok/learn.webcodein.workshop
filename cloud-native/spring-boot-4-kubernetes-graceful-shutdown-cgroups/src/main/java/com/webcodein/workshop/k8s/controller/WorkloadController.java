package com.webcodein.workshop.k8s.controller;

import com.webcodein.workshop.k8s.model.PodLifecycleState;
import com.webcodein.workshop.k8s.model.WorkloadTask;
import com.webcodein.workshop.k8s.service.ReadinessStateHolder;
import com.webcodein.workshop.k8s.service.WorkloadProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    private final WorkloadProcessingService workloadProcessingService;
    private final ReadinessStateHolder readinessStateHolder;

    public WorkloadController(WorkloadProcessingService workloadProcessingService,
                              ReadinessStateHolder readinessStateHolder) {
        this.workloadProcessingService = workloadProcessingService;
        this.readinessStateHolder = readinessStateHolder;
    }

    @PostMapping
    public ResponseEntity<?> submitWorkload(@RequestBody Map<String, Object> request) {
        if (workloadProcessingService.isShuttingDown()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Node is shutting down; requests are rejected."));
        }

        String payload = Optional.ofNullable(request.get("payload"))
                .map(Object::toString)
                .orElse("default-task");
        int durationMs = Optional.ofNullable(request.get("durationMs"))
                .map(val -> Integer.parseInt(val.toString()))
                .orElse(100);

        WorkloadTask task = workloadProcessingService.submitTask(payload, durationMs);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/active")
    public ResponseEntity<PodLifecycleState> getActiveStatus() {
        String podName = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("local-pod");
        String readiness = readinessStateHolder.getReadinessState() != null
                ? readinessStateHolder.getReadinessState().name()
                : "UNKNOWN";
        String liveness = readinessStateHolder.getLivenessState() != null
                ? readinessStateHolder.getLivenessState().name()
                : "UNKNOWN";

        PodLifecycleState state = new PodLifecycleState(
                podName,
                readiness,
                liveness,
                workloadProcessingService.getActiveCount(),
                workloadProcessingService.isShuttingDown()
        );

        return ResponseEntity.ok(state);
    }
}
