package com.webcodein.ecommerce.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
        this.productCode = productCode;
        this.quantity = quantity;
        this.status = "PENDING";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getProductCode() { return productCode; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
