package com.webcodein.hikaricp.controller;

import com.webcodein.hikaricp.dto.PoolMetricsResponse;
import com.webcodein.hikaricp.service.PoolMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pool")
public class PoolMetricsController {

    private final PoolMetricsService metricsService;

    public PoolMetricsController(PoolMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<PoolMetricsResponse> getMetrics() {
        return ResponseEntity.ok(metricsService.getPoolMetrics());
    }
}
