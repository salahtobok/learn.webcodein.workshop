package com.bookstore.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders") // "order" is often a reserved keyword in SQL
public class Order {
    @Id
    private UUID orderId; // The unique identity
    private String status;
    private String shippingAddress;
    
    protected Order() {} // For JPA

    public Order(UUID orderId) {
        this.orderId = Objects.requireNonNull(orderId);
        this.status = "PENDING";
    }

    public void complete() {
        this.status = "COMPLETED";
    }
    
    public boolean isReadyForCheckout() {
        return "PENDING".equals(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
