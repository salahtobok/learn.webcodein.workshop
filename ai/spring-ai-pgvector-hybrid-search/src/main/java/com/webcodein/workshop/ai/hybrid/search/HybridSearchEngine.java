package com.webcodein.workshop.ai.hybrid.search;

import com.webcodein.workshop.ai.hybrid.model.DocumentRecord;
import com.webcodein.workshop.ai.hybrid.model.SearchResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class HybridSearchEngine {

    private final Map<String, DocumentRecord> store = new ConcurrentHashMap<>();

    public void indexDocument(DocumentRecord document) {
        store.put(document.id(), document);
    }

    public List<SearchResult> searchDense(List<Double> queryEmbedding, int topK) {
        return store.values().stream()
                .map(doc -> {
                    double similarity = cosineSimilarity(queryEmbedding, doc.embedding());
                    return new SearchResult(doc.id(), doc.title(), doc.content(), similarity, "DENSE_VECTOR");
                })
                .sorted()
                .limit(topK)
                .collect(Collectors.toList());
    }

    public List<SearchResult> searchKeyword(String query, int topK) {
        String lowerQuery = query.toLowerCase();
        String[] terms = lowerQuery.split("\\s+");

        return store.values().stream()
                .map(doc -> {
                    double score = computeBm25LikeScore(terms, doc.content().toLowerCase(), doc.title().toLowerCase());
                    return new SearchResult(doc.id(), doc.title(), doc.content(), score, "SPARSE_BM25");
                })
                .filter(res -> res.score() > 0.0)
                .sorted()
                .limit(topK)
                .collect(Collectors.toList());
    }

    public List<SearchResult> searchHybrid(String query, List<Double> queryEmbedding, int topK, double rrfK) {
        List<SearchResult> denseResults = searchDense(queryEmbedding, topK * 2);
        List<SearchResult> keywordResults = searchKeyword(query, topK * 2);

        Map<String, Double> rrfScores = new HashMap<>();
        Map<String, DocumentRecord> docMap = new HashMap<>();

        // 1. Process Dense Ranks
        for (int rank = 0; rank < denseResults.size(); rank++) {
            SearchResult res = denseResults.get(rank);
            double rrf = 1.0 / (rrfK + (rank + 1));
            rrfScores.put(res.documentId(), rrfScores.getOrDefault(res.documentId(), 0.0) + rrf);
            docMap.put(res.documentId(), store.get(res.documentId()));
        }

        // 2. Process Keyword BM25 Ranks
        for (int rank = 0; rank < keywordResults.size(); rank++) {
            SearchResult res = keywordResults.get(rank);
            double rrf = 1.0 / (rrfK + (rank + 1));
            rrfScores.put(res.documentId(), rrfScores.getOrDefault(res.documentId(), 0.0) + rrf);
            docMap.put(res.documentId(), store.get(res.documentId()));
        }

        // 3. Sort by combined RRF score
        return rrfScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topK)
                .map(entry -> {
                    DocumentRecord doc = docMap.get(entry.getKey());
                    return new SearchResult(doc.id(), doc.title(), doc.content(), entry.getValue(), "HYBRID_RRF");
                })
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size() || v1.isEmpty()) {
            return 0.0;
        }
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private double computeBm25LikeScore(String[] terms, String content, String title) {
        double score = 0.0;
        for (String term : terms) {
            if (term.isBlank()) continue;
            if (title.contains(term)) {
                score += 3.0; // Title boost
            }
            if (content.contains(term)) {
                score += 1.0;
            }
        }
        return score;
    }

    public int getDocumentCount() {
        return store.size();
    }
}
