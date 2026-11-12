package com.webcodein.ota;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("OTA Rollout Redis Testcontainers Integration Suite")
class OtaRolloutIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should execute legacy canary rollout flow")
    void shouldExecuteLegacyCanaryRolloutFlow() throws Exception {
        long deviceId = 123456L;
        String version = "v2.0";

        // Check initially - not eligible
        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", String.valueOf(deviceId))
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("No updates available"));

        // Enable for device
        mockMvc.perform(post("/api/ota/enable/{version}", version)
                        .param("deviceId", String.valueOf(deviceId)))
                .andExpect(status().isOk());

        // Check again - eligible
        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", String.valueOf(deviceId))
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("Update available for " + version));
    }

    @Test
    @DisplayName("Should evaluate structured JSON payload with CDN download URL")
    void shouldEvaluateStructuredJsonPayload() throws Exception {
        long deviceId = 88001L;
        String version = "v3.1.0";

        // Enable in Redis
        mockMvc.perform(post("/api/ota/enable/{version}", version)
                        .param("deviceId", String.valueOf(deviceId)))
                .andExpect(status().isOk());

        String jsonPayload = """
                {
                    "deviceId": 88001,
                    "targetVersion": "v3.1.0",
                    "deviceModel": "Pixel-Tablet",
                    "currentVersion": "v3.0.0"
                }
                """;

        mockMvc.perform(post("/api/ota/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.deviceId").value(88001))
                .andExpect(jsonPath("$.targetVersion").value("v3.1.0"))
                .andExpect(jsonPath("$.downloadUrl").value("https://cdn.webcodein.com/firmware/Pixel-Tablet/v3.1.0/update.bin"))
                .andExpect(jsonPath("$.rolloutPhase").value("ACTIVE_CANARY"));
    }

    @Test
    @DisplayName("Should disable device and instantly revoke eligibility")
    void shouldDisableDeviceAndRevokeEligibility() throws Exception {
        long deviceId = 99001L;
        String version = "v4.0.0";

        // Enable
        mockMvc.perform(post("/api/ota/enable/{version}", version)
                        .param("deviceId", String.valueOf(deviceId)))
                .andExpect(status().isOk());

        // Verify enabled
        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", String.valueOf(deviceId))
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("Update available for " + version));

        // Disable
        mockMvc.perform(post("/api/ota/disable/{version}", version)
                        .param("deviceId", String.valueOf(deviceId)))
                .andExpect(status().isOk());

        // Verify revoked
        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", String.valueOf(deviceId))
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("No updates available"));
    }

    @Test
    @DisplayName("Should track rollout stats via BITCOUNT")
    void shouldTrackRolloutStatsViaBitCount() throws Exception {
        String version = "v5.0.0";

        // Enable 3 devices
        mockMvc.perform(post("/api/ota/enable/{version}", version).param("deviceId", "10")).andExpect(status().isOk());
        mockMvc.perform(post("/api/ota/enable/{version}", version).param("deviceId", "20")).andExpect(status().isOk());
        mockMvc.perform(post("/api/ota/enable/{version}", version).param("deviceId", "30")).andExpect(status().isOk());

        mockMvc.perform(get("/api/ota/stats/{version}", version)
                        .param("fleetSize", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetVersion").value(version))
                .andExpect(jsonPath("$.enabledDeviceCount").value(3))
                .andExpect(jsonPath("$.estimatedFleetSize").value(1000))
                .andExpect(jsonPath("$.effectivePercentage").value(0.3));
    }

    @Test
    @DisplayName("Should purge rollout key completely upon emergency halt")
    void shouldEmergencyHaltRollout() throws Exception {
        long deviceId = 77001L;
        String version = "v6.0.0";

        // Enable
        mockMvc.perform(post("/api/ota/enable/{version}", version)
                        .param("deviceId", String.valueOf(deviceId)))
                .andExpect(status().isOk());

        // Emergency Halt
        mockMvc.perform(delete("/api/ota/emergency-halt/{version}", version))
                .andExpect(status().isNoContent());

        // Verify device is immediately ineligible
        mockMvc.perform(get("/api/ota/check")
                        .param("deviceId", String.valueOf(deviceId))
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("No updates available"));
    }
}
