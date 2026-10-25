package com.webcodein.concurrency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.concurrency.model.OrderRequest;
import com.webcodein.concurrency.web.GlobalExceptionHandler;
import com.webcodein.concurrency.web.OrderCheckoutController;
import com.webcodein.concurrency.web.ScopedRequestContextFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class OrderCheckoutIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private OrderCheckoutController orderCheckoutController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderCheckoutController)
                .addFilters(new ScopedRequestContextFilter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should successfully process order using Structured Concurrency and propagate ScopedValue")
    void shouldSuccessfullyProcessOrderUsingStructuredConcurrencyAndScopedValue() throws Exception {
        String correlationId = "corr-" + UUID.randomUUID();
        OrderRequest request = new OrderRequest(
                "ORD-9910",
                "CUST-1001",
                new BigDecimal("199.99"),
                3
        );

        mockMvc.perform(post("/api/orders/checkout")
                        .header("X-Correlation-Id", correlationId)
                        .header("X-User-Id", "alice")
                        .header("X-Tenant-Id", "tenant-eu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORD-9910"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.inventoryReservationId", startsWith("INV-")))
                .andExpect(jsonPath("$.paymentTransactionId", startsWith("PAY-")))
                .andExpect(jsonPath("$.riskScore").value(12))
                .andExpect(jsonPath("$.correlationId").value(correlationId))
                .andExpect(jsonPath("$.durationMs", lessThan(500)));
    }

    @Test
    @DisplayName("Should fail fast and cancel remaining subtasks when fraud is detected")
    void shouldFailFastAndCancelRemainingSubtasksWhenFraudDetected() throws Exception {
        String correlationId = "corr-fraud-" + UUID.randomUUID();
        OrderRequest request = new OrderRequest(
                "ORD-FRAUD-1",
                "SUSPICIOUS_USER",
                new BigDecimal("499.00"),
                1
        );

        mockMvc.perform(post("/api/orders/checkout")
                        .header("X-Correlation-Id", correlationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FRAUD_DETECTED"))
                .andExpect(jsonPath("$.riskScore").value(95))
                .andExpect(jsonPath("$.correlationId").value(correlationId));
    }

    @Test
    @DisplayName("Should fail when payment exceeds single-transaction limit")
    void shouldFailWhenPaymentExceedsLimit() throws Exception {
        OrderRequest request = new OrderRequest(
                "ORD-EXPENSIVE",
                "CUST-LEGIT",
                new BigDecimal("75000.00"),
                1
        );

        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.error").value("PAYMENT_DECLINED"));
    }

    @Test
    @DisplayName("Should execute speculative race with anySuccessfulOrThrow and return fastest carrier")
    void shouldExecuteSpeculativeRaceAndReturnFastestQuote() throws Exception {
        String correlationId = "corr-race-" + UUID.randomUUID();

        mockMvc.perform(get("/api/shipping/quotes/fastest")
                        .header("X-Correlation-Id", correlationId)
                        .param("origin", "NYC")
                        .param("destination", "LON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carrierName").value("FastExpress Logistics"))
                .andExpect(jsonPath("$.transitDays").value(2))
                .andExpect(jsonPath("$.correlationId").value(correlationId));
    }

    @Test
    @DisplayName("Should isolate ScopedValue context across 20 concurrent threads without leaks")
    void shouldIsolateScopedValueContextAcrossConcurrentThreadsWithoutLeaks() throws Exception {
        int threadCount = 20;
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final String threadCorrId = "corr-thread-" + i;
            final String threadUserId = "user-" + i;

            futures.add(executor.submit(() -> {
                latch.await();
                mockMvc.perform(get("/api/context/current")
                                .header("X-Correlation-Id", threadCorrId)
                                .header("X-User-Id", threadUserId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.correlationId").value(threadCorrId))
                        .andExpect(jsonPath("$.userId").value(threadUserId));
                return true;
            }));
        }

        latch.countDown();

        for (Future<Boolean> future : futures) {
            assertThat(future.get(5, TimeUnit.SECONDS)).isTrue();
        }

        executor.shutdown();
    }
}
