package com.webcodein.dop.model;

public record PayPal(String email) implements PaymentMethod {
    public PayPal {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
    }
}
