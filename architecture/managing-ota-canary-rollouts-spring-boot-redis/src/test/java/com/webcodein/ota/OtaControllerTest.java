package com.webcodein.ota;

import com.webcodein.ota.model.CanaryCheckRequest;
import com.webcodein.ota.model.CanaryCheckResponse;
import com.webcodein.ota.model.RolloutStatsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OtaController.class)
@DisplayName("OtaController WebMvc Slice Tests")
class OtaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OtaRolloutService otaRolloutService;

    @Test
    @DisplayName("GET /api/ota/check should return legacy string response")
    void shouldReturnLegacyCheckResponse() throws Exception {
        when(otaRolloutService.isEligibleForUpdate(100L, "v2.0")).thenReturn(true);
        when(otaRolloutService.isEligibleForUpdate(200L, "v2.0")).thenReturn(false);

        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", "100")
                        .param("version", "v2.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Update available for v2.0"));

        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", "200")
                        .param("version", "v2.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("No updates available"));
    }

    @Test
    @DisplayName("POST /api/ota/evaluate should evaluate JSON request and return structured response")
    void shouldEvaluateStructuredJsonRequest() throws Exception {
        CanaryCheckResponse mockResponse = CanaryCheckResponse.eligible(
                123L, "v2.0.1", "https://cdn.webcodein.com/firmware/Pixel-9/v2.0.1/update.bin", "PHASE_1"
        );
        when(otaRolloutService.evaluateEligibility(any(CanaryCheckRequest.class))).thenReturn(mockResponse);

        String jsonPayload = """
                {
                    "deviceId": 123,
                    "targetVersion": "v2.0.1",
                    "deviceModel": "Pixel-9",
                    "currentVersion": "v2.0.0"
                }
                """;

        mockMvc.perform(post("/api/ota/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.deviceId").value(123))
                .andExpect(jsonPath("$.targetVersion").value("v2.0.1"))
                .andExpect(jsonPath("$.downloadUrl").value("https://cdn.webcodein.com/firmware/Pixel-9/v2.0.1/update.bin"))
                .andExpect(jsonPath("$.rolloutPhase").value("PHASE_1"));
    }

    @Test
    @DisplayName("POST /api/ota/enable/{version} should invoke service to enable device")
    void shouldEnableDevice() throws Exception {
        mockMvc.perform(post("/api/ota/enable/{version}", "v2.0")
                        .param("deviceId", "555"))
                .andExpect(status().isOk());

        verify(otaRolloutService).enableUpdateForDevice(555L, "v2.0");
    }

    @Test
    @DisplayName("POST /api/ota/disable/{version} should invoke service to disable device")
    void shouldDisableDevice() throws Exception {
        mockMvc.perform(post("/api/ota/disable/{version}", "v2.0")
                        .param("deviceId", "555"))
                .andExpect(status().isOk());

        verify(otaRolloutService).disableUpdateForDevice(555L, "v2.0");
    }

    @Test
    @DisplayName("GET /api/ota/stats/{version} should return adoption statistics")
    void shouldReturnRolloutStats() throws Exception {
        RolloutStatsResponse mockStats = RolloutStatsResponse.of("v2.0", 100_000L, 1_000_000L);
        when(otaRolloutService.getRolloutStats("v2.0", 1_000_000L)).thenReturn(mockStats);

        mockMvc.perform(get("/api/ota/stats/{version}", "v2.0")
                        .param("fleetSize", "1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetVersion").value("v2.0"))
                .andExpect(jsonPath("$.enabledDeviceCount").value(100000))
                .andExpect(jsonPath("$.estimatedFleetSize").value(1000000))
                .andExpect(jsonPath("$.effectivePercentage").value(10.0));
    }

    @Test
    @DisplayName("DELETE /api/ota/emergency-halt/{version} should return 204 when key deleted")
    void shouldEmergencyHaltRollout() throws Exception {
        when(otaRolloutService.emergencyHaltRollout("v2.0")).thenReturn(true);
        when(otaRolloutService.emergencyHaltRollout("v9.9")).thenReturn(false);

        mockMvc.perform(delete("/api/ota/emergency-halt/{version}", "v2.0"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/ota/emergency-halt/{version}", "v9.9"))
                .andExpect(status().isNotFound());
    }
}
