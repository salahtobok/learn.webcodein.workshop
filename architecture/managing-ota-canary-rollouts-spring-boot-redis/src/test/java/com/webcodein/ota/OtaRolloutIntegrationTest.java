package com.webcodein.ota;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
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
    void testCanaryRolloutFlow() throws Exception {
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
}
