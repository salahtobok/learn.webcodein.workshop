package com.webcodein.workshop.fullstack.controller;

import com.webcodein.workshop.fullstack.service.StockTickerStreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StockStreamControllerTest {

    private MockMvc mockMvc;
    private StockTickerStreamService streamService;

    @BeforeEach
    void setUp() {
        streamService = new StockTickerStreamService();
        StockStreamController controller = new StockStreamController(streamService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/stream/stats should return initial stream stats")
    void shouldReturnStreamStats() throws Exception {
        mockMvc.perform(get("/api/stream/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeEmitters").value(0))
                .andExpect(jsonPath("$.virtualThreadsEnabled").value(true));
    }

    @Test
    @DisplayName("POST /api/stream/publish should broadcast update")
    void shouldPublishPriceUpdate() throws Exception {
        String payload = """
                {
                    "ticker": "MSFT",
                    "price": 425.80,
                    "changePercent": 0.55,
                    "sequenceNumber": 500
                }
                """;

        mockMvc.perform(post("/api/stream/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("MSFT"))
                .andExpect(jsonPath("$.deliveredToClients").value(0))
                .andExpect(jsonPath("$.sequenceNumber").value(500));
    }

    @Test
    @DisplayName("GET /api/stream/prices should initiate SSE stream")
    void shouldInitiateSseStream() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/stream/prices?clientId=test-user-1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        assertThat(streamService.getActiveEmitterCount()).isEqualTo(1);
    }
}
