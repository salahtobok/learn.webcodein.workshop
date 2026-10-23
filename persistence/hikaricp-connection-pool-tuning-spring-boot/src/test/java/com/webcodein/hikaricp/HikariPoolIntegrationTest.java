package com.webcodein.hikaricp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.hikaricp.dto.OrderRequest;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class HikariPoolIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(post("/api/orders/leak/release"));
    }

    @Test
    @DisplayName("Verify HikariCP production configuration properties")
    void testHikariConfigurationProperties() {
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

        assertThat(hikariDataSource.getPoolName()).isEqualTo("ProductionHikariPool");
        assertThat(hikariDataSource.getMaximumPoolSize()).isEqualTo(10);
        assertThat(hikariDataSource.getMinimumIdle()).isEqualTo(10);
        assertThat(hikariDataSource.getConnectionTimeout()).isEqualTo(3000L);
        assertThat(hikariDataSource.getMaxLifetime()).isEqualTo(1800000L);
        assertThat(hikariDataSource.getIdleTimeout()).isEqualTo(600000L);
        assertThat(hikariDataSource.getLeakDetectionThreshold()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("Verify order creation endpoint releases connection back to pool")
    void testCreateOrderSuccessfully() throws Exception {
        OrderRequest request = new OrderRequest("Alice Walker", "PROD-101", new BigDecimal("149.99"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Alice Walker"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Pool metrics endpoint
        mockMvc.perform(get("/api/pool/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poolName").value("ProductionHikariPool"))
                .andExpect(jsonPath("$.maximumPoolSize").value(10))
                .andExpect(jsonPath("$.minimumIdle").value(10));
    }

    @Test
    @DisplayName("Verify concurrent order processing under pool capacity")
    void testConcurrentOrderProcessing() throws Exception {
        int numberOfRequests = 20;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Callable<Integer>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            final int index = i;
            tasks.add(() -> {
                OrderRequest request = new OrderRequest("Customer-" + index, "PROD-BULK", new BigDecimal("49.99"));
                return mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andReturn().getResponse().getStatus();
            });
        }

        List<Future<Integer>> results = executor.invokeAll(tasks);
        executor.shutdown();

        for (Future<Integer> result : results) {
            assertThat(result.get()).isEqualTo(201);
        }
    }

    @Test
    @DisplayName("Verify connection leak simulation and release")
    void testConnectionLeakSimulation() throws Exception {
        mockMvc.perform(get("/api/orders/leak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Wait past leak threshold to trigger watchdog logging
        Thread.sleep(2200);

        mockMvc.perform(post("/api/orders/leak/release"))
                .andExpect(status().isOk());
    }
}
