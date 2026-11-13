package com.webcodein.workshop.protocol;

import com.webcodein.workshop.protocol.controller.ProtocolBenchmarkController;
import com.webcodein.workshop.protocol.service.BenchmarkExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProtocolBenchmarkControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BenchmarkExecutionService service = new BenchmarkExecutionService();
        ProtocolBenchmarkController controller = new ProtocolBenchmarkController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/benchmark/health returns UP")
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/benchmark/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("POST /api/benchmark/rest returns REST benchmark result")
    void testRestBenchmark() throws Exception {
        mockMvc.perform(post("/api/benchmark/rest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocol").value("REST_JSON"))
                .andExpect(jsonPath("$.payloadSizeBytes").isNumber());
    }

    @Test
    @DisplayName("GET /api/benchmark/comparison returns comparative metrics")
    void testComparison() throws Exception {
        mockMvc.perform(get("/api/benchmark/comparison"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payloadReductionPercent").isNumber())
                .andExpect(jsonPath("$.recommendation").exists());
    }
}
