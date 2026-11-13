package com.webcodein.workshop.ai.hybrid.controller;

import com.webcodein.workshop.ai.hybrid.model.DocumentRecord;
import com.webcodein.workshop.ai.hybrid.model.SearchResult;
import com.webcodein.workshop.ai.hybrid.service.DocumentSearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class DocumentSearchController {

    private final DocumentSearchService searchService;

    public DocumentSearchController(DocumentSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/hybrid")
    public ResponseEntity<List<SearchResult>> hybridSearch(
            @RequestParam String query,
            @RequestBody List<Double> queryEmbedding,
            @RequestParam(defaultValue = "5") int topK
    ) {
        List<SearchResult> results = searchService.executeHybridSearch(query, queryEmbedding, topK);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/dense")
    public ResponseEntity<List<SearchResult>> denseSearch(
            @RequestBody List<Double> queryEmbedding,
            @RequestParam(defaultValue = "5") int topK
    ) {
        List<SearchResult> results = searchService.executeDenseSearch(queryEmbedding, topK);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/keyword")
    public ResponseEntity<List<SearchResult>> keywordSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK
    ) {
        List<SearchResult> results = searchService.executeKeywordSearch(query, topK);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/documents")
    public ResponseEntity<Map<String, String>> addDocument(@Valid @RequestBody DocumentRecord document) {
        searchService.addDocument(document);
        return ResponseEntity.ok(Map.of("message", "Document indexed successfully", "id", document.id()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "indexedDocuments", searchService.getTotalDocumentCount(),
                "searchMode", "BM25_DENSE_HYBRID_RRF"
        ));
    }
}
