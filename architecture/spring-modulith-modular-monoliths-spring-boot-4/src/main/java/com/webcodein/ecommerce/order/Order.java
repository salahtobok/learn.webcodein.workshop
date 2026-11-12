package com.webcodein.ecommerce.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productCode;
    private int quantity;
    private String status;

    public Order() {}

    public Order(String productCode, int quantity) {
        Objects.requireNonNull(productCode, "Product code must not be null");
        if (productCode.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero: " + quantity);
        }
        this.productCode = productCode;
        this.quantity = quantity;
        this.status = "PENDING";
    }

    public void complete() {
        if ("CANCELLED".equals(this.status)) {
            throw new IllegalStateException("Cannot complete an already cancelled order");
        }
        this.status = "COMPLETED";
    }

    public void cancel() {
        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException("Cannot cancel an already completed order");
        }
        this.status = "CANCELLED";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getProductCode() { return productCode; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
