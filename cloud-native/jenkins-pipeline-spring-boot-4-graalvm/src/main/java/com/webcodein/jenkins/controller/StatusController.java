package com.webcodein.jenkins.controller;

import com.webcodein.jenkins.model.BuildPipelineStatus;
import com.webcodein.jenkins.service.PipelineStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {

    private final PipelineStatusService pipelineStatusService;

    public StatusController(PipelineStatusService pipelineStatusService) {
        this.pipelineStatusService = pipelineStatusService;
    }

    @GetMapping("/status")
    public ResponseEntity<BuildPipelineStatus> getStatus() {
        return ResponseEntity.ok(pipelineStatusService.getCurrentStatus());
    }
}
