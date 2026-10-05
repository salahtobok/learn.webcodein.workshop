package com.webcodein.flashing.controller;

import com.webcodein.flashing.model.FlashAudit;
import com.webcodein.flashing.repository.FlashAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FlashAuditControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlashAuditRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldLogFlashEvent() throws Exception {
        String jsonPayload = """
            {
                "deviceSerialNumber": "DEV12345",
                "partitionName": "boot",
                "imageChecksum": "abcdef123456",
                "successful": true
            }
            """;

        mockMvc.perform(post("/api/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceSerialNumber").value("DEV12345"))
                .andExpect(jsonPath("$.partitionName").value("boot"));
    }

    @Test
    void shouldGetDeviceHistory() throws Exception {
        FlashAudit audit = new FlashAudit("DEV12345", "system", "checksumX", true);
        repository.save(audit);

        mockMvc.perform(get("/api/audit/DEV12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].partitionName").value("system"));
    }
}
