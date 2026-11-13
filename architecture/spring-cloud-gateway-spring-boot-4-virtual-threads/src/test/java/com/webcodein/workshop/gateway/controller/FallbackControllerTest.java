package com.webcodein.workshop.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FallbackControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        FallbackController controller = new FallbackController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /fallback/{routeId} should return graceful fallback response with degraded header")
    void shouldReturnFallbackResponse() throws Exception {
        mockMvc.perform(get("/fallback/orders-service"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Gateway-Degraded", "true"))
                .andExpect(jsonPath("$.routeId").value("orders-service"))
                .andExpect(jsonPath("$.degraded").value(true))
                .andExpect(jsonPath("$.status").value("SERVICE_UNAVAILABLE_FALLBACK"));
    }
}
