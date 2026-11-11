package com.webcodein.ddd.domain.model.order;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
        // Required by Hibernate
    }

    public Order(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
    }

    public void addItem(UUID productId, int quantity, double price) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot modify an order that is not pending.");
        }
        if (this.items.size() >= 100) {
            throw new IllegalStateException("An order cannot contain more than 100 distinct items.");
        }
        this.items.add(new OrderItem(productId, quantity, price));
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed.");
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an empty order.");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel an order that has already shipped.");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(OrderItem::subtotal)
                .sum();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
