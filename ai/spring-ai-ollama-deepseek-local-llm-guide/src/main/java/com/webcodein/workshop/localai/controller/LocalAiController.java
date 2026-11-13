package com.webcodein.workshop.localai.controller;

import com.webcodein.workshop.localai.model.DocumentAnalysisResult;
import com.webcodein.workshop.localai.model.InferenceRequest;
import com.webcodein.workshop.localai.model.InferenceResponse;
import com.webcodein.workshop.localai.service.DocumentComplianceService;
import com.webcodein.workshop.localai.service.LocalInferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/local-ai")
public class LocalAiController {

    private final LocalInferenceService inferenceService;
    private final DocumentComplianceService complianceService;

    public LocalAiController(LocalInferenceService inferenceService, DocumentComplianceService complianceService) {
        this.inferenceService = inferenceService;
        this.complianceService = complianceService;
    }

    @PostMapping("/generate")
    public ResponseEntity<InferenceResponse> generate(@RequestBody InferenceRequest request) {
        InferenceResponse response = inferenceService.executeLocalPrompt(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze-document")
    public ResponseEntity<DocumentAnalysisResult> analyzeDocument(
            @RequestParam String documentId,
            @RequestBody String content
    ) {
        DocumentAnalysisResult result = complianceService.auditDocument(documentId, content);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "backend", "Ollama Local Daemon",
                "models", new String[]{"deepseek-r1:8b", "llama3.2:3b", "mistral:7b"}
        ));
    }
}
