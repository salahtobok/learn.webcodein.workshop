package com.webcodein.workshop.architecture.archunitenforceddd.application.service;

import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Money;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.OrderLine;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String customerId, String productCode, BigDecimal price, String currency) {
        Order order = new Order(UUID.randomUUID(), customerId);
        order.addLine(new OrderLine(UUID.randomUUID(), productCode, new Money(price, currency)));
        orderRepository.save(order);
        return order;
    }

    public Order payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.pay();
        orderRepository.save(order);
        return order;
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrder(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}
