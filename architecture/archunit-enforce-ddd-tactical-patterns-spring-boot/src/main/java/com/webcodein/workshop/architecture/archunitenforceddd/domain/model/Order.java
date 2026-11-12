package com.webcodein.workshop.architecture.archunitenforceddd.domain.model;

import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.AggregateRoot;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {} // JPA

    public Order(UUID id, String customerId) {
        this.id = Objects.requireNonNull(id, "Order ID must not be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.status = OrderStatus.CREATED;
    }

    public void addLine(OrderLine line) {
        Objects.requireNonNull(line, "Order line must not be null");
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add lines to an order in status: " + this.status);
        }
        this.lines.add(line);
    }

    public void pay() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be transitioned to PAID");
        }
        if (this.lines.isEmpty()) {
            throw new IllegalStateException("Cannot pay for an order with no lines");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public enum OrderStatus {
        CREATED, PAID, CANCELLED
    }
}
