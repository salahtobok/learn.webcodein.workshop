package com.webcodein.workshop.ai.hybrid.controller;

import com.webcodein.workshop.ai.hybrid.search.HybridSearchEngine;
import com.webcodein.workshop.ai.hybrid.service.DocumentSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentSearchControllerTest {

    private MockMvc mockMvc;
    private DocumentSearchService searchService;

    @BeforeEach
    void setUp() {
        HybridSearchEngine engine = new HybridSearchEngine();
        searchService = new DocumentSearchService(engine);
        DocumentSearchController controller = new DocumentSearchController(searchService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/search/keyword should return keyword results")
    void shouldReturnKeywordResults() throws Exception {
        mockMvc.perform(get("/api/search/keyword?query=OAuth2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentId").value("DOC-101"))
                .andExpect(jsonPath("$[0].matchType").value("SPARSE_BM25"));
    }

    @Test
    @DisplayName("POST /api/search/hybrid should execute RRF search")
    void shouldExecuteHybridSearch() throws Exception {
        String queryVecJson = "[0.9, 0.8, 0.1, 0.05]";

        mockMvc.perform(post("/api/search/hybrid?query=Security")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(queryVecJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchType").value("HYBRID_RRF"));
    }

    @Test
    @DisplayName("POST /api/search/documents should index new document")
    void shouldIndexNewDocument() throws Exception {
        String newDocJson = """
                {
                    "id": "DOC-999",
                    "title": "New Item",
                    "content": "Content",
                    "metadata": {},
                    "embedding": [0.5, 0.5]
                }
                """;

        mockMvc.perform(post("/api/search/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDocJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("DOC-999"));
    }

    @Test
    @DisplayName("GET /api/search/stats should return engine health")
    void shouldReturnStats() throws Exception {
        mockMvc.perform(get("/api/search/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.searchMode").value("BM25_DENSE_HYBRID_RRF"));
    }
}
