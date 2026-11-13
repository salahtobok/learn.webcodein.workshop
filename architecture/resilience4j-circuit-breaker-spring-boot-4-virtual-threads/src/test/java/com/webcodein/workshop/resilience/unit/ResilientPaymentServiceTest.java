package com.webcodein.workshop.resilience.unit;

import com.webcodein.workshop.resilience.config.ResilienceConfig;
import com.webcodein.workshop.resilience.model.PaymentRequest;
import com.webcodein.workshop.resilience.model.PaymentResponse;
import com.webcodein.workshop.resilience.service.ExternalPaymentGateway;
import com.webcodein.workshop.resilience.service.MockExternalPaymentGateway;
import com.webcodein.workshop.resilience.service.ResilientPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResilientPaymentServiceTest {

    private ResilienceConfig resilienceConfig;
    private ExternalPaymentGateway mockGateway;
    private ResilientPaymentService paymentService;

    @BeforeEach
    void setUp() {
        resilienceConfig = new ResilienceConfig();
        mockGateway = new MockExternalPaymentGateway();
        paymentService = new ResilientPaymentService(
                mockGateway,
                resilienceConfig.circuitBreakerRegistry(),
                resilienceConfig.retryRegistry(),
                resilienceConfig.bulkheadRegistry()
        );
    }

    @Test
    @DisplayName("Should process payment successfully under normal conditions")
    void shouldProcessPaymentSuccessfully() {
        PaymentRequest request = new PaymentRequest("TX-1", "ACC-1", new BigDecimal("100.00"), "USD");
        PaymentResponse response = paymentService.processPayment(request);

        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.degradedFallback()).isFalse();

        Map<String, Object> metrics = paymentService.getCircuitBreakerMetrics();
        assertThat(metrics.get("state")).isEqualTo("CLOSED");
        assertThat(metrics.get("numberOfSuccessfulCalls")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should trip circuit breaker from CLOSED to OPEN after consecutive failures and return fallback")
    void shouldTripCircuitBreakerToOpen() {
        // Trigger failures
        mockGateway.setSimulateFailure(true);

        for (int i = 1; i <= 5; i++) {
            PaymentRequest req = new PaymentRequest("TX-FAIL-" + i, "ACC-" + i, new BigDecimal("100.00"), "USD");
            PaymentResponse res = paymentService.processPayment(req);
            assertThat(res.degradedFallback()).isTrue();
            assertThat(res.status()).isEqualTo("QUEUED_DEGRADED");
        }

        Map<String, Object> metrics = paymentService.getCircuitBreakerMetrics();
        assertThat(metrics.get("state")).isEqualTo("OPEN");
        assertThat((Float) metrics.get("failureRate")).isGreaterThanOrEqualTo(50.0f);

        // When circuit is OPEN, calls must be rejected immediately without calling gateway
        PaymentRequest blockedReq = new PaymentRequest("TX-BLOCKED", "ACC-BLOCKED", new BigDecimal("50.00"), "USD");
        PaymentResponse blockedRes = paymentService.processPayment(blockedReq);
        assertThat(blockedRes.degradedFallback()).isTrue();
        assertThat(blockedRes.message()).contains("Circuit breaker is OPEN");
    }

    @Test
    @DisplayName("Should reset circuit breaker metrics and state")
    void shouldResetCircuitBreaker() {
        mockGateway.setSimulateFailure(true);
        paymentService.processPayment(new PaymentRequest("TX-ERR-1", "ACC-1", new BigDecimal("10.00"), "USD"));

        paymentService.resetCircuitBreaker();
        Map<String, Object> metrics = paymentService.getCircuitBreakerMetrics();
        assertThat(metrics.get("state")).isEqualTo("CLOSED");
        assertThat(metrics.get("numberOfBufferedCalls")).isEqualTo(0);
    }
}
