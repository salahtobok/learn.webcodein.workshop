package com.webcodein.workshop.mcp.model;

import java.util.Objects;

public record InventoryItem(
        String sku,
        String name,
        int stockQuantity,
        String warehouseLocation,
        boolean inStock
) {
    public InventoryItem {
        Objects.requireNonNull(sku, "SKU must not be null");
        Objects.requireNonNull(name, "Name must not be null");
        Objects.requireNonNull(warehouseLocation, "Warehouse location must not be null");
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    public static InventoryItem of(String sku, String name, int stockQuantity, String warehouseLocation) {
        return new InventoryItem(sku, name, stockQuantity, warehouseLocation, stockQuantity > 0);
    }
}
