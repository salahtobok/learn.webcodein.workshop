package com.webcodein.workshop.k8s;

import com.webcodein.workshop.k8s.controller.WorkloadController;
import com.webcodein.workshop.k8s.model.WorkloadTask;
import com.webcodein.workshop.k8s.service.ReadinessStateHolder;
import com.webcodein.workshop.k8s.service.WorkloadProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WorkloadControllerTest {

    @Mock
    private WorkloadProcessingService workloadProcessingService;

    @Mock
    private ReadinessStateHolder readinessStateHolder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        WorkloadController controller = new WorkloadController(workloadProcessingService, readinessStateHolder);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/workloads returns 201 Created when node is healthy")
    void submitWorkloadSuccess() throws Exception {
        WorkloadTask task = new WorkloadTask("test-uuid-1", "order-job", 100, "PROCESSING");
        when(workloadProcessingService.isShuttingDown()).thenReturn(false);
        when(workloadProcessingService.submitTask(anyString(), anyInt())).thenReturn(task);

        String jsonPayload = """
                {
                  "payload": "order-job",
                  "durationMs": 100
                }
                """;

        mockMvc.perform(post("/api/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-uuid-1"))
                .andExpect(jsonPath("$.payload").value("order-job"))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    @DisplayName("POST /api/workloads returns 503 Service Unavailable when shutting down")
    void submitWorkloadRejectedDuringShutdown() throws Exception {
        when(workloadProcessingService.isShuttingDown()).thenReturn(true);

        String jsonPayload = """
                {
                  "payload": "order-job",
                  "durationMs": 100
                }
                """;

        mockMvc.perform(post("/api/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/workloads/active returns pod lifecycle status")
    void getActiveStatusReturnsState() throws Exception {
        when(readinessStateHolder.getReadinessState()).thenReturn(ReadinessState.ACCEPTING_TRAFFIC);
        when(readinessStateHolder.getLivenessState()).thenReturn(LivenessState.CORRECT);
        when(workloadProcessingService.getActiveCount()).thenReturn(3);
        when(workloadProcessingService.isShuttingDown()).thenReturn(false);

        mockMvc.perform(get("/api/workloads/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readinessStatus").value("ACCEPTING_TRAFFIC"))
                .andExpect(jsonPath("$.livenessStatus").value("CORRECT"))
                .andExpect(jsonPath("$.inFlightRequests").value(3))
                .andExpect(jsonPath("$.shuttingDown").value(false));
    }
}
