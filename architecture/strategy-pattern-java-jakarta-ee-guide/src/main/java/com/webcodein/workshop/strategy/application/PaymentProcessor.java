package com.webcodein.workshop.strategy.application;

import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import com.webcodein.workshop.strategy.domain.PaymentStrategy;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
public class PaymentProcessor {

    private final List<PaymentStrategy> strategies;

    @Inject
    public PaymentProcessor(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentResult processPayment(PaymentRequest request) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(request.type()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No payment strategy found for type: " + request.type()))
                .process(request);
    }
}
