package com.webcodein.workshop.architecture.hexagonal.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Pure domain model representing an Order.
 * Domain entities must remain completely decoupled from Spring, Jakarta Persistence, or Web frameworks.
 */
public class Order {
    private final UUID id;
    private final String product;
    private final int quantity;

    public Order(UUID id, String product, int quantity) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (product == null || product.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
