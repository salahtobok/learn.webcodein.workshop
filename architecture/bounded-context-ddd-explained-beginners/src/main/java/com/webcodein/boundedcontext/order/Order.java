package com.webcodein.boundedcontext.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;
    private UUID customerId;
    private String status;

    protected Order() {} // JPA requirement

    public Order(UUID customerId) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.status = "CREATED";
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    
    public void confirmOrder() {
        this.status = "CONFIRMED";
    }
}
