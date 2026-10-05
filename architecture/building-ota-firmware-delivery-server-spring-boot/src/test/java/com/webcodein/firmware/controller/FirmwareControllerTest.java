package com.webcodein.firmware.controller;

import com.webcodein.firmware.model.Firmware;
import com.webcodein.firmware.repository.FirmwareRepository;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FirmwareControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FirmwareRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository.deleteAll();
        
        // Ensure data directory exists for tests
        Path storageDir = Paths.get("data/firmware");
        Files.createDirectories(storageDir);
        File testFile = storageDir.resolve("update_v2.bin").toFile();
        if (!testFile.exists()) {
            Files.write(testFile.toPath(), new byte[1024 * 5]); // 5KB dummy file
        }

        Firmware f = new Firmware("2.0.0", "PhoneX", "update_v2.bin", "checksum123", 5120, true);
        repository.save(f);
    }

    @Test
    void shouldReturnLatestFirmware() throws Exception {
        mockMvc.perform(get("/api/firmware/latest").param("modelName", "PhoneX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2.0.0"))
                .andExpect(jsonPath("$.checksum").value("checksum123"));
    }
}
