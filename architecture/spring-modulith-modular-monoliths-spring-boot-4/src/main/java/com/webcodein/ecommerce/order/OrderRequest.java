package com.webcodein.ecommerce.order;

import java.util.Objects;

/**
 * Immutable request payload for placing an e-commerce order.
 *
 * @param productCode Unique SKU or product identifier
 * @param quantity    Positive integer quantity
 */
public record OrderRequest(String productCode, int quantity) {
    public OrderRequest {
        Objects.requireNonNull(productCode, "Product code must not be null");
        if (productCode.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero: " + quantity);
        }
    }
}
