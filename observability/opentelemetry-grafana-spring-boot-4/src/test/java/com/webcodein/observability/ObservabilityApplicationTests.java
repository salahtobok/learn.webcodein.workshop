package com.webcodein.observability;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ObservabilityApplicationTests {

    @Container
    static GenericContainer<?> otelCollector = new GenericContainer<>("otel/opentelemetry-collector:0.104.0")
            .withExposedPorts(4318);

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void otlpProperties(DynamicPropertyRegistry registry) {
        registry.add("management.otlp.tracing.endpoint", () -> 
            String.format("http://%s:%d/v1/traces", otelCollector.getHost(), otelCollector.getMappedPort(4318)));
        registry.add("management.otlp.metrics.endpoint", () -> 
            String.format("http://%s:%d/v1/metrics", otelCollector.getHost(), otelCollector.getMappedPort(4318)));
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders?id=12345"))
                .andExpect(status().is2xxSuccessful());
    }
}
