package com.webcodein.workshop.ddd.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrderItem(UUID itemId, String productCode, Money unitPrice, int quantity) {

    public OrderItem {
        Objects.requireNonNull(itemId, "Item ID cannot be null");
        Objects.requireNonNull(productCode, "Product code cannot be null");
        if (productCode.isBlank()) {
            throw new IllegalArgumentException("Product code cannot be blank");
        }
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero, given: " + quantity);
        }
    }

    public static OrderItem of(String productCode, Money unitPrice, int quantity) {
        return new OrderItem(UUID.randomUUID(), productCode, unitPrice, quantity);
    }

    public Money lineTotal() {
        return unitPrice.multiply(quantity);
    }
}
