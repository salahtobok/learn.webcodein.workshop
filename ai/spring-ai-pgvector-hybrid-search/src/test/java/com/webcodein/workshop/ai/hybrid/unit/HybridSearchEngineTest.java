package com.webcodein.workshop.ai.hybrid.unit;

import com.webcodein.workshop.ai.hybrid.model.DocumentRecord;
import com.webcodein.workshop.ai.hybrid.model.SearchResult;
import com.webcodein.workshop.ai.hybrid.search.HybridSearchEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HybridSearchEngineTest {

    private HybridSearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        searchEngine = new HybridSearchEngine();
        searchEngine.indexDocument(new DocumentRecord(
                "DOC-A",
                "Kubernetes Ingress and Gateway API",
                "Deep dive into configuring cloud-native edge routing on Kubernetes clusters.",
                Map.of(),
                List.of(0.9, 0.8, 0.1)
        ));

        searchEngine.indexDocument(new DocumentRecord(
                "DOC-B",
                "Product SKU-990-PRO SSD NVMe",
                "Hardware technical specifications for SKU-990-PRO PCIe Gen 4 solid state drive.",
                Map.of(),
                List.of(0.1, 0.2, 0.95)
        ));
    }

    @Test
    @DisplayName("Keyword search should find exact SKU that dense search might miss")
    void shouldFindExactSkuViaKeywordSearch() {
        List<SearchResult> results = searchEngine.searchKeyword("SKU-990-PRO", 5);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).documentId()).isEqualTo("DOC-B");
        assertThat(results.get(0).matchType()).isEqualTo("SPARSE_BM25");
    }

    @Test
    @DisplayName("Dense search should rank semantically similar document first")
    void shouldFindSemanticMatchViaDenseSearch() {
        List<Double> queryVec = List.of(0.85, 0.75, 0.15); // Close to DOC-A
        List<SearchResult> results = searchEngine.searchDense(queryVec, 5);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).documentId()).isEqualTo("DOC-A");
        assertThat(results.get(0).matchType()).isEqualTo("DENSE_VECTOR");
    }

    @Test
    @DisplayName("Hybrid RRF search should merge both signals and rank highest relevance document first")
    void shouldMergeSignalsWithReciprocalRankFusion() {
        List<Double> queryVec = List.of(0.88, 0.79, 0.12);
        List<SearchResult> hybridResults = searchEngine.searchHybrid("Kubernetes Ingress", queryVec, 5, 60.0);

        assertThat(hybridResults).isNotEmpty();
        assertThat(hybridResults.get(0).documentId()).isEqualTo("DOC-A");
        assertThat(hybridResults.get(0).matchType()).isEqualTo("HYBRID_RRF");
        assertThat(hybridResults.get(0).score()).isGreaterThan(0.0);
    }
}
