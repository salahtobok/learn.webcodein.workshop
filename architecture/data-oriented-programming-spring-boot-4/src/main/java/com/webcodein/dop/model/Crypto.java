package com.webcodein.dop.model;

public record Crypto(String walletAddress, String currency) implements PaymentMethod {
    public Crypto {
        if (walletAddress == null || walletAddress.isBlank()) {
            throw new IllegalArgumentException("Wallet address must not be blank");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
    }
}
