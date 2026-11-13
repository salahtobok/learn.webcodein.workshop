package com.webcodein.workshop.k8s.controller;

import com.webcodein.workshop.k8s.service.ReadinessStateHolder;
import com.webcodein.workshop.k8s.service.WorkloadProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/probes")
public class ProbeManagementController {

    private final ReadinessStateHolder readinessStateHolder;
    private final WorkloadProcessingService workloadProcessingService;

    public ProbeManagementController(ReadinessStateHolder readinessStateHolder,
                                     WorkloadProcessingService workloadProcessingService) {
        this.readinessStateHolder = readinessStateHolder;
        this.workloadProcessingService = workloadProcessingService;
    }

    @PostMapping("/drain")
    public ResponseEntity<Map<String, String>> initiateDrain() {
        readinessStateHolder.markOutOfService();
        workloadProcessingService.markShuttingDown();
        return ResponseEntity.ok(Map.of(
                "status", "DRAINING",
                "readiness", "REFUSING_TRAFFIC",
                "message", "PreStop drain initiated. Pod is excluded from Service Endpoints."
        ));
    }

    @PostMapping("/restore")
    public ResponseEntity<Map<String, String>> restoreTraffic() {
        readinessStateHolder.markInService();
        workloadProcessingService.resetShutdown();
        return ResponseEntity.ok(Map.of(
                "status", "ACTIVE",
                "readiness", "ACCEPTING_TRAFFIC",
                "message", "Pod restored to active service endpoints."
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getProbeStatus() {
        return ResponseEntity.ok(Map.of(
                "readiness", readinessStateHolder.getReadinessState() != null ? readinessStateHolder.getReadinessState().name() : "UNKNOWN",
                "liveness", readinessStateHolder.getLivenessState() != null ? readinessStateHolder.getLivenessState().name() : "UNKNOWN",
                "activeTasks", workloadProcessingService.getActiveCount(),
                "shuttingDown", workloadProcessingService.isShuttingDown()
        ));
    }
}
