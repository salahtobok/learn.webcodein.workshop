package com.webcodein.ecommerce.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    private String productCode;
    private int stockQuantity;

    public InventoryItem() {}

    public InventoryItem(String productCode, int stockQuantity) {
        Objects.requireNonNull(productCode, "Product code must not be null");
        if (productCode.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity must not be negative: " + stockQuantity);
        }
        this.productCode = productCode;
        this.stockQuantity = stockQuantity;
    }

    public String getProductCode() { return productCode; }
    public int getStockQuantity() { return stockQuantity; }

    public void reduceStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Reduction amount must be greater than zero: " + amount);
        }
        if (this.stockQuantity < amount) {
            throw new IllegalArgumentException("Not enough stock: requested=" + amount + ", available=" + this.stockQuantity);
        }
        this.stockQuantity -= amount;
    }

    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Addition amount must be greater than zero: " + amount);
        }
        this.stockQuantity += amount;
    }
}
