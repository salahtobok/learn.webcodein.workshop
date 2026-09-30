package com.webcodein.cqrs.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "order_read_model")
public class OrderReadModel {

    @Id
    private UUID id;
    private String customerId;
    private double amount;
    private String status;

    public OrderReadModel() {}

    public OrderReadModel(UUID id, String customerId, double amount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
