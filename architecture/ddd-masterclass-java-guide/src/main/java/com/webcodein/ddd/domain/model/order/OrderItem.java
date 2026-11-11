package com.webcodein.ddd.domain.model.order;

import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record OrderItem(UUID productId, int quantity, double price) {
    public OrderItem {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public double subtotal() {
        return quantity * price;
    }
}
