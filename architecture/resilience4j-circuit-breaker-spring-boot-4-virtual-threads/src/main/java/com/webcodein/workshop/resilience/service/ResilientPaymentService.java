package com.webcodein.workshop.resilience.service;

import com.webcodein.workshop.resilience.config.ResilienceConfig;
import com.webcodein.workshop.resilience.model.PaymentRequest;
import com.webcodein.workshop.resilience.model.PaymentResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
public class ResilientPaymentService {

    private static final Logger log = LoggerFactory.getLogger(ResilientPaymentService.class);

    private final ExternalPaymentGateway externalGateway;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final Bulkhead bulkhead;

    public ResilientPaymentService(
            ExternalPaymentGateway externalGateway,
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry,
            BulkheadRegistry bulkheadRegistry
    ) {
        this.externalGateway = externalGateway;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(ResilienceConfig.PAYMENT_CIRCUIT_BREAKER);
        this.retry = retryRegistry.retry(ResilienceConfig.PAYMENT_RETRY);
        this.bulkhead = bulkheadRegistry.bulkhead(ResilienceConfig.PAYMENT_BULKHEAD);
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        Supplier<PaymentResponse> paymentSupplier = () -> externalGateway.processRemotePayment(request);

        // Decorate with Bulkhead -> CircuitBreaker -> Retry
        Supplier<PaymentResponse> decorated = Bulkhead.decorateSupplier(bulkhead,
                CircuitBreaker.decorateSupplier(circuitBreaker,
                        Retry.decorateSupplier(retry, paymentSupplier)));

        try {
            return decorated.get();
        } catch (CallNotPermittedException ex) {
            log.warn("Circuit breaker is OPEN. Fast-failing payment: {}", request.transactionId());
            return PaymentResponse.fallback(request.transactionId(), request.amount(), request.currency(), "Circuit breaker is OPEN: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Execution failed for payment {}: {}", request.transactionId(), ex.getMessage());
            return PaymentResponse.fallback(request.transactionId(), request.amount(), request.currency(), ex.getMessage());
        }
    }

    public Map<String, Object> getCircuitBreakerMetrics() {
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        return Map.of(
                "name", circuitBreaker.getName(),
                "state", circuitBreaker.getState().name(),
                "failureRate", metrics.getFailureRate(),
                "slowCallRate", metrics.getSlowCallRate(),
                "numberOfBufferedCalls", metrics.getNumberOfBufferedCalls(),
                "numberOfFailedCalls", metrics.getNumberOfFailedCalls(),
                "numberOfSuccessfulCalls", metrics.getNumberOfSuccessfulCalls(),
                "numberOfNotPermittedCalls", metrics.getNumberOfNotPermittedCalls()
        );
    }

    public void setSimulateFailure(boolean simulate) {
        externalGateway.setSimulateFailure(simulate);
    }

    public boolean isSimulateFailure() {
        return externalGateway.isSimulateFailure();
    }

    public void resetCircuitBreaker() {
        circuitBreaker.reset();
    }
}
