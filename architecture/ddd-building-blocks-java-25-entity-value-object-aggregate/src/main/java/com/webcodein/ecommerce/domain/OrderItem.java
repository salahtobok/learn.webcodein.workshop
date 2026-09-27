package com.webcodein.ecommerce.domain;

import java.util.UUID;
import java.util.Objects;

// This is an Entity, but it is NOT an Aggregate Root.
// It only makes sense within the context of an Order.
public class OrderItem {
    private final UUID orderItemId;
    private final UUID productId;
    private String productName;
    private Money unitPrice;
    private int quantity;

    public OrderItem(UUID orderItemId, UUID productId, String productName, Money unitPrice, int quantity) {
        this.orderItemId = Objects.requireNonNull(orderItemId);
        this.productId = Objects.requireNonNull(productId);
        this.productName = productName;
        this.unitPrice = unitPrice;
        setQuantity(quantity);
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be at least 1");
        this.quantity = quantity;
    }

    public Money calculateTotal() {
        // Multiply unit price by quantity
        return Money.of(
            unitPrice.amount().multiply(java.math.BigDecimal.valueOf(quantity)).toString(), 
            unitPrice.currencyCode()
        );
    }

    public UUID getOrderItemId() { return orderItemId; }
    public int getQuantity() { return quantity; }
}
