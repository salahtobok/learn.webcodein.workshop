package com.bookstore.shipping.domain;

public record Book(
    String isbn, 
    double weightKg, 
    Dimensions dimensions
) {
    public boolean requiresOversizePackaging() {
        return weightKg > 5.0 || dimensions.isOversized();
    }
}
