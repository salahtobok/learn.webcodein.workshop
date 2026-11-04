package com.webcodein.boundedcontext.order;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;
    private UUID customerId;
    private String status;
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {} // JPA requirement

    public Order(UUID customerId) {
        this.id = UUID.randomUUID();
        this.customerId = Objects.requireNonNull(customerId, "CustomerId cannot be null");
        this.status = "CREATED";
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public void addItem(OrderItem item) {
        if (!"CREATED".equals(this.status)) {
            throw new IllegalStateException("Cannot add items to order in status: " + this.status);
        }
        Objects.requireNonNull(item, "OrderItem cannot be null");
        this.items.add(item);
    }

    public double calculateTotal() {
        return items.stream()
            .mapToDouble(OrderItem::getSubtotal)
            .sum();
    }

    public void confirmOrder() {
        if (!"CREATED".equals(this.status)) {
            throw new IllegalStateException("Only CREATED orders can be confirmed, current status: " + this.status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order with zero items");
        }
        this.status = "CONFIRMED";
    }

    public void cancelOrder() {
        if ("CONFIRMED".equals(this.status)) {
            throw new IllegalStateException("Cannot cancel an already confirmed order");
        }
        this.status = "CANCELLED";
    }
}
