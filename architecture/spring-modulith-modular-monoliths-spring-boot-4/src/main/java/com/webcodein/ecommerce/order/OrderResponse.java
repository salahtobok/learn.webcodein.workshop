package com.webcodein.ecommerce.order;

import java.util.Objects;

/**
 * Immutable response projection for order resource queries.
 */
public record OrderResponse(Long id, String productCode, int quantity, String status) {
    public OrderResponse {
        Objects.requireNonNull(id, "Order ID must not be null");
        Objects.requireNonNull(productCode, "Product code must not be null");
        Objects.requireNonNull(status, "Status must not be null");
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getProductCode(), order.getQuantity(), order.getStatus());
    }
}
