package com.webcodein.ecommerce.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    private String productCode;
    private int stockQuantity;

    public InventoryItem() {}

    public InventoryItem(String productCode, int stockQuantity) {
        this.productCode = productCode;
        this.stockQuantity = stockQuantity;
    }

    public String getProductCode() { return productCode; }
    public int getStockQuantity() { return stockQuantity; }
    
    public void reduceStock(int amount) {
        if (this.stockQuantity < amount) {
            throw new IllegalArgumentException("Not enough stock");
        }
        this.stockQuantity -= amount;
    }
}
