package com.webcodein.workshop.strategy.presentation;

import com.webcodein.workshop.strategy.application.PaymentProcessor;
import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentProcessor paymentProcessor;

    public PaymentController(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    @PostMapping
    public ResponseEntity<PaymentResult> processPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentResult result = paymentProcessor.processPayment(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new PaymentResult(false, null, e.getMessage()));
        }
    }
}
