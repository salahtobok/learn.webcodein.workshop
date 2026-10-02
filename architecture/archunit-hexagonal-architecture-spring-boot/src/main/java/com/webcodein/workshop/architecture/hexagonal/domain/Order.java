package com.webcodein.workshop.architecture.hexagonal.domain;

import java.util.UUID;

// Domain entities must not depend on Spring, JPA, or web annotations.
public class Order {
    private final UUID id;
    private final String product;
    private final int quantity;

    public Order(UUID id, String product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public UUID getId() { return id; }
    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
}
