package com.webcodein.workshop.ota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OtaUpdateControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FirmwareVersionRepository versionRepo;

    @Autowired
    private DeltaPatchRepository patchRepo;

    @BeforeEach
    void setUp() {
        patchRepo.deleteAll();
        versionRepo.deleteAll();

        FirmwareVersion v1 = versionRepo.save(new FirmwareVersion("1.0.0", "sha-full-1", Instant.now().minusSeconds(200000)));
        FirmwareVersion v2 = versionRepo.save(new FirmwareVersion("1.1.0", "sha-full-2", Instant.now().minusSeconds(100000)));
        FirmwareVersion v3 = versionRepo.save(new FirmwareVersion("1.2.0", "sha-full-3", Instant.now()));

        // Only delta patch from 1.1.0 -> 1.2.0 exists; no delta from 1.0.0 -> 1.2.0
        patchRepo.save(new DeltaPatch(v2, v3, "patch-1.1-to-1.2.bsdiff", 18_000_000L, "sha-patch-1.1-1.2"));
    }

    @Test
    @DisplayName("Should return delta patch when patch exists between current and latest version")
    void testCheckUpdate_HasDeltaPatch() throws Exception {
        mockMvc.perform(get("/api/v1/ota/check")
                        .param("currentVersion", "1.1.0")
                        .param("deviceModel", "PhoneX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetVersion").value("1.2.0"))
                .andExpect(jsonPath("$.downloadUrl").value("https://s3.amazonaws.com/ota-bucket/patch-1.1-to-1.2.bsdiff"))
                .andExpect(jsonPath("$.patchSha256").value("sha-patch-1.1-1.2"))
                .andExpect(jsonPath("$.sizeBytes").value(18000000));
    }

    @Test
    @DisplayName("Should fallback to full ROM when no delta patch exists to latest version")
    void testCheckUpdate_FallbackToFullRom() throws Exception {
        mockMvc.perform(get("/api/v1/ota/check")
                        .param("currentVersion", "1.0.0")
                        .param("deviceModel", "PhoneX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetVersion").value("1.2.0"))
                .andExpect(jsonPath("$.downloadUrl").value("https://s3.amazonaws.com/ota-bucket/full-rom-1.2.0.zip"))
                .andExpect(jsonPath("$.patchSha256").value("sha-full-3"))
                .andExpect(jsonPath("$.sizeBytes").value(2000000000L));
    }

    @Test
    @DisplayName("Should return HTTP 204 No Content when device is already up to date")
    void testCheckUpdate_AlreadyUpToDate() throws Exception {
        mockMvc.perform(get("/api/v1/ota/check")
                        .param("currentVersion", "1.2.0")
                        .param("deviceModel", "PhoneX"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return HTTP 204 No Content when current version is unknown")
    void testCheckUpdate_UnknownVersion() throws Exception {
        mockMvc.perform(get("/api/v1/ota/check")
                        .param("currentVersion", "0.5.0")
                        .param("deviceModel", "PhoneX"))
                .andExpect(status().isNoContent());
    }
}
