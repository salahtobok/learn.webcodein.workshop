package com.webcodein.workshop.ddd.application;

import com.webcodein.workshop.ddd.domain.event.OrderPlacedEvent;
import com.webcodein.workshop.ddd.domain.event.OrderShippedEvent;
import com.webcodein.workshop.ddd.domain.model.CustomerId;
import com.webcodein.workshop.ddd.domain.model.Money;
import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderId;
import com.webcodein.workshop.ddd.domain.repository.OrderDomainRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class OrderApplicationService {

    private final OrderDomainRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderApplicationService(OrderDomainRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public OrderId createOrder(CustomerId customerId, String currency, String initialProduct, Money initialPrice, int quantity) {
        Order order = Order.create(customerId, currency);
        order.addItem(initialProduct, initialPrice, quantity);
        order.confirm();

        Order saved = orderRepository.save(order);
        eventPublisher.publishEvent(OrderPlacedEvent.now(saved.getId(), saved.calculateTotal()));
        return saved.getId();
    }

    public void applyDiscount(OrderId orderId, Money discount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
        order.applyDiscount(discount);
        orderRepository.save(order);
    }

    public void shipOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
        order.ship();
        orderRepository.save(order);
        eventPublisher.publishEvent(OrderShippedEvent.now(order.getId()));
    }

    public Order getOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
    }
}
