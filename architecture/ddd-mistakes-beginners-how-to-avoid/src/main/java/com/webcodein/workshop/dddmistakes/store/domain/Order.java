package com.webcodein.workshop.dddmistakes.store.domain;
import java.util.UUID;
import java.util.List;
public class Order {
    private UUID orderId;
    private UUID customerId;
    private List<OrderItem> lineItems;
    public Order(UUID orderId, UUID customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }
}