package com.webcodein.workshop.ai.hybrid.unit;

import com.webcodein.workshop.ai.hybrid.model.DocumentRecord;
import com.webcodein.workshop.ai.hybrid.model.SearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentRecordTest {

    @Test
    @DisplayName("Should create valid DocumentRecord")
    void shouldCreateValidDocumentRecord() {
        DocumentRecord doc = new DocumentRecord("D-1", "Test Title", "Sample Content", Map.of("key", "val"), List.of(0.1, 0.2, 0.3));

        assertThat(doc.id()).isEqualTo("D-1");
        assertThat(doc.title()).isEqualTo("Test Title");
        assertThat(doc.embedding()).containsExactly(0.1, 0.2, 0.3);
    }

    @Test
    @DisplayName("Should reject blank document ID")
    void shouldRejectBlankId() {
        assertThatThrownBy(() -> new DocumentRecord("", "Title", "Content", Map.of(), List.of(0.1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Document ID cannot be blank");
    }

    @Test
    @DisplayName("Should reject empty embedding vector")
    void shouldRejectEmptyEmbedding() {
        assertThatThrownBy(() -> new DocumentRecord("D-1", "Title", "Content", Map.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Embedding vector must contain elements");
    }

    @Test
    @DisplayName("SearchResult compareTo should sort in descending score order")
    void shouldSortSearchResultDescending() {
        SearchResult s1 = new SearchResult("D-1", "Doc 1", "Snippet 1", 0.85, "DENSE");
        SearchResult s2 = new SearchResult("D-2", "Doc 2", "Snippet 2", 0.95, "DENSE");

        assertThat(s2.compareTo(s1)).isLessThan(0); // s2 is higher rank than s1
    }
}
