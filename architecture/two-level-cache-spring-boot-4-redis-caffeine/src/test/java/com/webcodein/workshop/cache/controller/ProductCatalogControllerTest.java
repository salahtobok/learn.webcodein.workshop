package com.webcodein.workshop.cache.controller;

import com.webcodein.workshop.cache.cache.TwoLevelCacheManager;
import com.webcodein.workshop.cache.service.ProductCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductCatalogControllerTest {

    private MockMvc mockMvc;
    private ProductCatalogService catalogService;

    @BeforeEach
    void setUp() {
        TwoLevelCacheManager cacheManager = new TwoLevelCacheManager();
        catalogService = new ProductCatalogService(cacheManager);
        ProductCatalogController controller = new ProductCatalogController(catalogService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/products/PROD-101 should return cached product")
    void shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/PROD-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROD-101"))
                .andExpect(jsonPath("$.name").value("Ultra High-Speed Cloud Router"));
    }

    @Test
    @DisplayName("GET /api/products/UNKNOWN should return 404")
    void shouldReturn404ForUnknownProduct() throws Exception {
        mockMvc.perform(get("/api/products/UNKNOWN-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/products/PROD-101 should update product and evict caches")
    void shouldUpdateProduct() throws Exception {
        String payload = """
                {
                    "id": "PROD-101",
                    "name": "Updated Router v2",
                    "price": 549.99,
                    "stockQuantity": 45
                }
                """;

        mockMvc.perform(put("/api/products/PROD-101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Router v2"));
    }

    @Test
    @DisplayName("GET /api/products/cache/metrics should return cache hit stats")
    void shouldReturnCacheMetrics() throws Exception {
        mockMvc.perform(get("/api/products/cache/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.l1Hits").exists())
                .andExpect(jsonPath("$.l2Hits").exists())
                .andExpect(jsonPath("$.dbLoads").exists());
    }

    @Test
    @DisplayName("POST /api/products/cache/clear-l1 should clear local Caffeine cache")
    void shouldClearL1() throws Exception {
        mockMvc.perform(post("/api/products/cache/clear-l1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
