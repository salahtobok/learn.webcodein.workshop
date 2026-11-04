package com.webcodein.boundedcontext.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    private UUID id;
    private String sku;
    private String productName;
    private double unitPrice;
    private int quantity;

    protected OrderItem() {} // JPA requirement

    public OrderItem(String sku, String productName, double unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice < 0.0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.id = UUID.randomUUID();
        this.sku = Objects.requireNonNull(sku, "SKU cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
