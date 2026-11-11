package com.webcodein.workshop.ddd.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Order {

    private Long id;
    private final Email customerEmail;
    private final Money totalAmount;
    private OrderStatus status;

    public Order(Email customerEmail, Money totalAmount) {
        if (customerEmail == null) {
            throw new IllegalArgumentException("Customer email is required");
        }
        if (totalAmount == null || totalAmount.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.CREATED;
    }

    public Order(String customerEmail, BigDecimal totalAmount) {
        this(new Email(customerEmail), Money.usd(totalAmount));
    }

    public static Order reconstitute(Long id, Email customerEmail, Money totalAmount, OrderStatus status) {
        Order order = new Order(customerEmail, totalAmount);
        order.id = id;
        order.status = status;
        return order;
    }

    public void fulfill() {
        if (this.status != OrderStatus.CREATED && this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Only CREATED or PAID orders can be fulfilled (current status: " + this.status + ")");
        }
        this.status = OrderStatus.FULFILLED;
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.FULFILLED) {
            throw new IllegalStateException("Cannot cancel an order that has already been fulfilled");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public Email getCustomerEmail() {
        return customerEmail;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : super.hashCode();
    }
}
