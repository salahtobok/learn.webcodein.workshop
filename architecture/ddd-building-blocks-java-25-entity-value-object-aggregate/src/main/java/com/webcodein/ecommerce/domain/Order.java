package com.webcodein.ecommerce.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collections;

public class Order {
    private final UUID orderId; // Global Identity
    private final UUID customerId; // Reference to another Aggregate Root
    private final List<OrderItem> items;
    private OrderStatus status;

    public enum OrderStatus { CREATED, PAID, SHIPPED, CANCELLED }

    public Order(UUID orderId, UUID customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
    }

    // Business method to add items. We do not expose the raw list!
    public void addProduct(UUID productId, String name, Money price, int quantity) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to an order that is not in CREATED status");
        }
        
        OrderItem newItem = new OrderItem(UUID.randomUUID(), productId, name, price, quantity);
        this.items.add(newItem);
    }

    // Calculating the total dynamically based on inner entities
    public Money calculateTotalAmount() {
        if (items.isEmpty()) return Money.of("0", "USD");
        
        Money total = items.get(0).calculateTotal();
        for (int i = 1; i < items.size(); i++) {
            total = total.add(items.get(i).calculateTotal());
        }
        return total;
    }

    // Read-only view of the items to protect the invariant
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public UUID getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
}
