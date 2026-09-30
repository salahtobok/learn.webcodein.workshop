package com.webcodein.dop.model;

public record CreditCard(String cardNumber, String expiryDate) implements PaymentMethod {}
