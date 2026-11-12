package com.webcodein.workshop.ddd.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {

    private final OrderId id;
    private final CustomerId customerId;
    private final String currency;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private Money discount;

    public Order(OrderId id, CustomerId customerId, String currency) {
        this.id = Objects.requireNonNull(id, "OrderId cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "CustomerId cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null").toUpperCase();
        this.status = OrderStatus.CREATED;
        this.discount = Money.zero(this.currency);
    }

    public static Order create(CustomerId customerId, String currency) {
        return new Order(OrderId.generate(), customerId, currency);
    }

    public void addItem(String productCode, Money unitPrice, int quantity) {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to order in status: " + this.status);
        }
        Objects.requireNonNull(unitPrice, "UnitPrice cannot be null");
        if (!unitPrice.currency().equalsIgnoreCase(this.currency)) {
            throw new IllegalArgumentException("Item currency " + unitPrice.currency() + " does not match order currency " + this.currency);
        }
        this.items.add(OrderItem.of(productCode, unitPrice, quantity));
    }

    public void applyDiscount(Money discountAmount) {
        if (this.status != OrderStatus.CREATED && this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot apply discount to order in status: " + this.status);
        }
        Objects.requireNonNull(discountAmount, "Discount cannot be null");
        if (!discountAmount.currency().equalsIgnoreCase(this.currency)) {
            throw new IllegalArgumentException("Discount currency " + discountAmount.currency() + " does not match order currency " + this.currency);
        }

        Money subtotal = calculateSubtotal();
        if (discountAmount.isGreaterThan(subtotal)) {
            throw new IllegalArgumentException("Discount amount (" + discountAmount.amount() + ") exceeds order subtotal (" + subtotal.amount() + ")");
        }

        this.discount = discountAmount;
    }

    public void confirm() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be confirmed, current status: " + this.status);
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order with zero items");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void ship() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order must be CONFIRMED before shipping, current status: " + this.status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel an order that has already been shipped");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        Objects.requireNonNull(reason, "Cancellation reason must be provided");
        this.status = OrderStatus.CANCELLED;
    }

    public Money calculateSubtotal() {
        Money subtotal = Money.zero(this.currency);
        for (OrderItem item : items) {
            subtotal = subtotal.add(item.lineTotal());
        }
        return subtotal;
    }

    public Money calculateTotal() {
        return calculateSubtotal().subtract(this.discount);
    }

    // Read-only getters
    public OrderId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getCurrency() {
        return currency;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Money getDiscount() {
        return discount;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
