package com.webcodein.dop.model;

public record PaymentRequest(double amount, PaymentMethod method) {
    public PaymentRequest {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method must not be null");
        }
    }
}
