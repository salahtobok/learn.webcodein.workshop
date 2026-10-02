package com.webcodein.workshop.architecture.hexagonal.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    @Id
    private UUID id;
    private String product;
    private int quantity;

    public OrderJpaEntity() {}

    public OrderJpaEntity(UUID id, String product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters omitted for brevity
}
