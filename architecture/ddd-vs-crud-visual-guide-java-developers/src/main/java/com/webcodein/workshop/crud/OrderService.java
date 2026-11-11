package com.webcodein.workshop.crud;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderEntity createOrder(String email, BigDecimal amount) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerEmail(email);
        order.setTotalAmount(amount);
        order.setStatus("CREATED");

        return repository.save(order);
    }

    public void fulfillOrder(Long id) {
        OrderEntity order = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));

        if (!"CREATED".equals(order.getStatus())) {
            throw new IllegalStateException("You cannot fulfill this order because it is not CREATED.");
        }
        order.setStatus("FULFILLED");
        repository.save(order);
    }

    public void cancelOrder(Long id) {
        OrderEntity order = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));

        if ("FULFILLED".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel a fulfilled order");
        }
        order.setStatus("CANCELLED");
        repository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderEntity findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> listOrders() {
        return repository.findAll();
    }
}
