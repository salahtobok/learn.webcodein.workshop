package com.webcodein.hikaricp.controller;

import com.webcodein.hikaricp.dto.PoolMetricsResponse;
import com.webcodein.hikaricp.service.PoolMetricsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoolMetricsController.class)
class PoolMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PoolMetricsService metricsService;

    @Test
    @DisplayName("GET /api/pool/metrics should return pool status details")
    void shouldReturnPoolMetrics() throws Exception {
        PoolMetricsResponse response = new PoolMetricsResponse(
                "ProductionHikariPool",
                2,
                8,
                10,
                0,
                10,
                10
        );

        when(metricsService.getPoolMetrics()).thenReturn(response);

        mockMvc.perform(get("/api/pool/metrics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poolName").value("ProductionHikariPool"))
                .andExpect(jsonPath("$.activeConnections").value(2))
                .andExpect(jsonPath("$.idleConnections").value(8))
                .andExpect(jsonPath("$.totalConnections").value(10))
                .andExpect(jsonPath("$.threadsAwaitingConnection").value(0))
                .andExpect(jsonPath("$.maximumPoolSize").value(10))
                .andExpect(jsonPath("$.minimumIdle").value(10));
    }
}
