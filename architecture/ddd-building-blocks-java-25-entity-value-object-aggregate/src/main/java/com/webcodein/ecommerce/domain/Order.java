package com.webcodein.ecommerce.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
        this.customerId = customerId;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }

    public void addItem(String productId, Money price, int quantity) {
        this.items.add(new OrderItem(productId, price, quantity));
    }
}
