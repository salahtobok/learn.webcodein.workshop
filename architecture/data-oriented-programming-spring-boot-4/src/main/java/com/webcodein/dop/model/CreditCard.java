package com.webcodein.dop.model;

public record CreditCard(String cardNumber, String expiryDate) implements PaymentMethod {
    public CreditCard {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number must not be blank");
        }
        if (expiryDate == null || expiryDate.isBlank()) {
            throw new IllegalArgumentException("Expiry date must not be blank");
        }
    }
}
