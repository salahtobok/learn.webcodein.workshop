package com.webcodein.ecommerce.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    private String customerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {}

    public Order(String customerId) {
        this.id = UUID.randomUUID().toString();
        this.customerId = Objects.requireNonNull(customerId, "customerId cannot be null");
    }

    public Order(String id, String customerId) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId cannot be null");
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public void addItem(String productId, Money price, int quantity) {
        Objects.requireNonNull(productId, "productId cannot be null");
        Objects.requireNonNull(price, "price cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.items.add(new OrderItem(productId, price, quantity));
    }

    public Money calculateTotal() {
        if (items.isEmpty()) {
            return Money.zero("USD");
        }
        String currency = items.getFirst().getPrice().currency();
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItem item : items) {
            sum = sum.add(item.getPrice().amount().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return Money.of(sum, currency);
    }
}
