package com.webcodein.cicd.controller;

import com.webcodein.cicd.model.DeploymentStatus;
import com.webcodein.cicd.service.DeploymentHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AppController {

    private final DeploymentHealthService deploymentHealthService;

    public AppController(DeploymentHealthService deploymentHealthService) {
        this.deploymentHealthService = deploymentHealthService;
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        DeploymentStatus status = deploymentHealthService.getCurrentStatus();
        return ResponseEntity.ok(Map.of(
                "status", status.status(),
                "version", status.version(),
                "hostname", status.hostname(),
                "message", status.message()
        ));
    }

    @GetMapping("/api/status/details")
    public ResponseEntity<DeploymentStatus> getStatusDetails() {
        return ResponseEntity.ok(deploymentHealthService.getCurrentStatus());
    }
}
