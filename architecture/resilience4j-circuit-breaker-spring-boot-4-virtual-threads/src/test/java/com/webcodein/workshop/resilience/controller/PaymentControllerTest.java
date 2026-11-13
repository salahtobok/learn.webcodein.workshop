package com.webcodein.workshop.resilience.controller;

import com.webcodein.workshop.resilience.config.ResilienceConfig;
import com.webcodein.workshop.resilience.service.MockExternalPaymentGateway;
import com.webcodein.workshop.resilience.service.ResilientPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private MockMvc mockMvc;
    private ResilientPaymentService paymentService;
    private MockExternalPaymentGateway gateway;

    @BeforeEach
    void setUp() {
        ResilienceConfig config = new ResilienceConfig();
        gateway = new MockExternalPaymentGateway();
        paymentService = new ResilientPaymentService(
                gateway,
                config.circuitBreakerRegistry(),
                config.retryRegistry(),
                config.bulkheadRegistry()
        );
        PaymentController controller = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/payments/process should successfully process valid payment")
    void shouldProcessPaymentSuccessfully() throws Exception {
        String payload = """
                {
                    "transactionId": "TX-501",
                    "accountNumber": "ACC-789",
                    "amount": 250.00,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TX-501"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.degradedFallback").value(false));
    }

    @Test
    @DisplayName("GET /api/payments/circuit-breaker-status should return state metrics")
    void shouldReturnCircuitBreakerMetrics() throws Exception {
        mockMvc.perform(get("/api/payments/circuit-breaker-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("paymentService"))
                .andExpect(jsonPath("$.state").exists());
    }

    @Test
    @DisplayName("POST /api/payments/simulate-failure should toggle gateway failure simulation")
    void shouldToggleFailureSimulation() throws Exception {
        mockMvc.perform(post("/api/payments/simulate-failure?enabled=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failureSimulationEnabled").value(true));
    }

    @Test
    @DisplayName("POST /api/payments/reset should reset circuit breaker state")
    void shouldResetCircuitBreaker() throws Exception {
        mockMvc.perform(post("/api/payments/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("reset successfully")));
    }
}
