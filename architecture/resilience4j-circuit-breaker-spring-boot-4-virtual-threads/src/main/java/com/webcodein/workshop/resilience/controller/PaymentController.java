package com.webcodein.workshop.resilience.controller;

import com.webcodein.workshop.resilience.model.PaymentRequest;
import com.webcodein.workshop.resilience.model.PaymentResponse;
import com.webcodein.workshop.resilience.service.ResilientPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final ResilientPaymentService paymentService;

    public PaymentController(ResilientPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/circuit-breaker-status")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatus() {
        return ResponseEntity.ok(paymentService.getCircuitBreakerMetrics());
    }

    @PostMapping("/simulate-failure")
    public ResponseEntity<Map<String, Object>> simulateFailure(@RequestParam boolean enabled) {
        paymentService.setSimulateFailure(enabled);
        return ResponseEntity.ok(Map.of(
                "message", "Remote gateway failure simulation toggled",
                "failureSimulationEnabled", enabled
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetCircuitBreaker() {
        paymentService.resetCircuitBreaker();
        return ResponseEntity.ok(Map.of("message", "Circuit breaker metrics and state reset successfully"));
    }
}
