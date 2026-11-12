package com.webcodein.virtualthreads.api;

import com.webcodein.virtualthreads.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport() {
        return ResponseEntity.ok(reportService.generateSingleReport());
    }

    @GetMapping("/batch")
    public ResponseEntity<List<Map<String, Object>>> generateBatch(
            @RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(reportService.generateBatchReports(count));
    }

    @GetMapping("/lock-safety")
    public ResponseEntity<Map<String, Object>> checkLockSafety() {
        return ResponseEntity.ok(reportService.executeWithReentrantLock());
    }
}
