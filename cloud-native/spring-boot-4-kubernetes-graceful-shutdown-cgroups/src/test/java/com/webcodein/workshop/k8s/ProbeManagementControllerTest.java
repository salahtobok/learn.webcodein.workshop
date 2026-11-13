package com.webcodein.workshop.k8s;

import com.webcodein.workshop.k8s.controller.ProbeManagementController;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProbeManagementControllerTest {

    @Mock
    private ReadinessStateHolder readinessStateHolder;

    @Mock
    private WorkloadProcessingService workloadProcessingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProbeManagementController controller = new ProbeManagementController(readinessStateHolder, workloadProcessingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/probes/drain should trigger drain and return REFUSING_TRAFFIC")
    void initiateDrainSuccess() throws Exception {
        mockMvc.perform(post("/api/probes/drain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAINING"))
                .andExpect(jsonPath("$.readiness").value("REFUSING_TRAFFIC"));

        verify(readinessStateHolder).markOutOfService();
        verify(workloadProcessingService).markShuttingDown();
    }

    @Test
    @DisplayName("POST /api/probes/restore should restore traffic and return ACCEPTING_TRAFFIC")
    void restoreTrafficSuccess() throws Exception {
        mockMvc.perform(post("/api/probes/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.readiness").value("ACCEPTING_TRAFFIC"));

        verify(readinessStateHolder).markInService();
        verify(workloadProcessingService).resetShutdown();
    }

    @Test
    @DisplayName("GET /api/probes/status should return probe status snapshot")
    void getStatusReturnsSnapshot() throws Exception {
        when(readinessStateHolder.getReadinessState()).thenReturn(ReadinessState.ACCEPTING_TRAFFIC);
        when(readinessStateHolder.getLivenessState()).thenReturn(LivenessState.CORRECT);
        when(workloadProcessingService.getActiveCount()).thenReturn(5);
        when(workloadProcessingService.isShuttingDown()).thenReturn(false);

        mockMvc.perform(get("/api/probes/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readiness").value("ACCEPTING_TRAFFIC"))
                .andExpect(jsonPath("$.liveness").value("CORRECT"))
                .andExpect(jsonPath("$.activeTasks").value(5))
                .andExpect(jsonPath("$.shuttingDown").value(false));
    }
}
