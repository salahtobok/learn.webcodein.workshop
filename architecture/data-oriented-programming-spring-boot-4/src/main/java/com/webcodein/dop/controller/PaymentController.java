package com.webcodein.dop.controller;

import com.webcodein.dop.model.PaymentRequest;
import com.webcodein.dop.service.PaymentProcessingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentProcessingService paymentService;

    public PaymentController(PaymentProcessingService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public String process(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}
