package com.webcodein.workshop.ota;

import org.junit.jupiter.api.BeforeEach;
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

        FirmwareVersion v1 = versionRepo.save(new FirmwareVersion("1.0.0", "sha-full-1", Instant.now().minusSeconds(100000)));
        FirmwareVersion v2 = versionRepo.save(new FirmwareVersion("1.1.0", "sha-full-2", Instant.now()));

        patchRepo.save(new DeltaPatch(v1, v2, "patch-1.0-to-1.1.bsdiff", 50000000L, "sha-patch-1"));
    }

    @Test
    void testCheckUpdate_HasDeltaPatch() throws Exception {
        mockMvc.perform(get("/api/v1/ota/check")
                .param("currentVersion", "1.0.0")
                .param("deviceModel", "PhoneX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetVersion").value("1.1.0"))
                .andExpect(jsonPath("$.downloadUrl").value("https://s3.amazonaws.com/ota-bucket/patch-1.0-to-1.1.bsdiff"))
                .andExpect(jsonPath("$.patchSha256").value("sha-patch-1"));
    }

    @Test
    void testCheckUpdate_AlreadyUpToDate() throws Exception {
        mockMvc.perform(get("/api/v1/ota/check")
                .param("currentVersion", "1.1.0")
                .param("deviceModel", "PhoneX"))
                .andExpect(status().isNoContent());
    }
}
