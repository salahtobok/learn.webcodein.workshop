package com.webcodein.workshop.ddd.domain;
import java.math.BigDecimal;

public class Order {
    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;

    public enum OrderStatus { CREATED, FULFILLED, CANCELLED }

    public Order(String customerEmail, BigDecimal totalAmount) {
        if (customerEmail == null || !customerEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.CREATED;
    }

    // Constructor for reconstitution
    public Order(Long id, String customerEmail, BigDecimal totalAmount, OrderStatus status) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public void fulfill() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be fulfilled");
        }
        this.status = OrderStatus.FULFILLED;
    }

    public Long getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
}
