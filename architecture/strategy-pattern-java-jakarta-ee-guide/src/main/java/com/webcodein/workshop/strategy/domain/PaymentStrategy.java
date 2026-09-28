package com.webcodein.workshop.strategy.domain;

/**
 * The Strategy interface.
 * Note: If using Java 25 sealed interfaces, you would seal it here
 * and list the permitted implementations.
 */
public interface PaymentStrategy {
    boolean supports(PaymentType type);
    PaymentResult process(PaymentRequest request);
}
