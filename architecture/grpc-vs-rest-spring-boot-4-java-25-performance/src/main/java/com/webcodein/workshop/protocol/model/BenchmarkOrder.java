package com.webcodein.workshop.protocol.model;

import java.util.Objects;

public record BenchmarkOrder(
        String orderId,
        String customerId,
        double amount,
        int itemCount,
        long createdAtEpochMs
) {
    public BenchmarkOrder {
        Objects.requireNonNull(orderId, "Order ID must not be null");
        Objects.requireNonNull(customerId, "Customer ID must not be null");
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (itemCount <= 0) {
            throw new IllegalArgumentException("Item count must be positive");
        }
    }

    public static BenchmarkOrder sample() {
        return new BenchmarkOrder("ORD-BENCH-99", "CUST-CORP-01", 1249.50, 4, System.currentTimeMillis());
    }
}
