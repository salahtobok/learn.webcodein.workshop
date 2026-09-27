package com.webcodein.workshop.dddmistakes.ecommerce.domain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
public class Order {
    private final UUID id;
    private final String customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    protected Order() {
        this.id = null;
        this.customerId = null;
        this.createdAt = null;
        this.items = new ArrayList<>();
    }
    public Order(UUID id, String customerId) {
        if (id == null || customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Order ID and Customer ID are required");
        }
        this.id = id;
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }
    public void addItem(Product product, int quantity) {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to an order that is not in CREATED state");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        OrderItem item = new OrderItem(product.getId(), product.getPrice(), quantity);
        this.items.add(item);
        recalculateTotal();
    }
    public void confirm() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only created orders can be confirmed");
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order with no items");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
    }
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Order has already shipped and cannot be canceled");
        }
        this.status = OrderStatus.CANCELED;
    }
    private void recalculateTotal() {
        this.totalAmount = this.items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public UUID getId() { return id; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}