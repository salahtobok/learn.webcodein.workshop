package com.webcodein.workshop.ai.hybrid.service;

import com.webcodein.workshop.ai.hybrid.model.DocumentRecord;
import com.webcodein.workshop.ai.hybrid.model.SearchResult;
import com.webcodein.workshop.ai.hybrid.search.HybridSearchEngine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentSearchService {

    private final HybridSearchEngine searchEngine;

    public DocumentSearchService(HybridSearchEngine searchEngine) {
        this.searchEngine = searchEngine;
        // Seed documents
        seedInitialKnowledgeBase();
    }

    public void addDocument(DocumentRecord document) {
        searchEngine.indexDocument(document);
    }

    public List<SearchResult> executeDenseSearch(List<Double> embedding, int topK) {
        return searchEngine.searchDense(embedding, topK);
    }

    public List<SearchResult> executeKeywordSearch(String query, int topK) {
        return searchEngine.searchKeyword(query, topK);
    }

    public List<SearchResult> executeHybridSearch(String query, List<Double> embedding, int topK) {
        return searchEngine.searchHybrid(query, embedding, topK, 60.0);
    }

    public int getTotalDocumentCount() {
        return searchEngine.getDocumentCount();
    }

    private void seedInitialKnowledgeBase() {
        searchEngine.indexDocument(new DocumentRecord(
                "DOC-101",
                "Spring Boot 4 Security OAuth2 Guide",
                "Comprehensive tutorial on configuring OAuth2 resource servers and JWT tokens with Spring Security 7.",
                Map.of("author", "WebCodein", "category", "Security"),
                List.of(0.95, 0.85, 0.12, 0.05)
        ));

        searchEngine.indexDocument(new DocumentRecord(
                "DOC-102",
                "Hardware Part Spec SKU-RTX-4090-TI",
                "Specifications for SKU-RTX-4090-TI graphics processing unit with 24GB GDDR6X VRAM.",
                Map.of("author", "HardwareTeam", "category", "Specs"),
                List.of(0.15, 0.22, 0.89, 0.94)
        ));

        searchEngine.indexDocument(new DocumentRecord(
                "DOC-103",
                "Virtual Threads in Spring Boot 4",
                "Deep dive into Java 25 virtual threads and high-concurrency non-blocking server architectures.",
                Map.of("author", "JVMTeam", "category", "Architecture"),
                List.of(0.91, 0.78, 0.15, 0.08)
        ));
    }
}
