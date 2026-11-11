package com.webcodein.observability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
    @DisplayName("Context loads successfully with OTel Collector container")
    void contextLoads() {
    }

    @Test
    @DisplayName("POST /api/orders processes order and records observation")
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders?id=12345"))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed 12345"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} retrieves order details with trace context")
    void testGetOrder() throws Exception {
        mockMvc.perform(get("/api/orders/12345"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/orders with id=fail returns 500 internal server error")
    void testFailedOrder() throws Exception {
        mockMvc.perform(post("/api/orders?id=fail"))
                .andExpect(status().is5xxServerError());
    }
}
