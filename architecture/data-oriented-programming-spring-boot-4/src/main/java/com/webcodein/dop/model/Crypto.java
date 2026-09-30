package com.webcodein.dop.model;

public record Crypto(String walletAddress, String currency) implements PaymentMethod {}
