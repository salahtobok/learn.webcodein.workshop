package com.webcodein.ecommerce.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher events;

    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher events) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository must not be null");
        this.events = Objects.requireNonNull(events, "ApplicationEventPublisher must not be null");
    }

    @Transactional
    public Order placeOrder(String productCode, int quantity) {
        Order order = new Order(productCode, quantity);
        order = orderRepository.save(order);

        events.publishEvent(new OrderPlacedEvent(order.getId(), productCode, quantity));

        return order;
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrder(Long orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return orderRepository.findById(orderId);
    }
}
