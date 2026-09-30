package com.webcodein.ecommerce.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher events;

    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher events) {
        this.orderRepository = orderRepository;
        this.events = events;
    }

    @Transactional
    public Order placeOrder(String productCode, int quantity) {
        Order order = new Order(productCode, quantity);
        order = orderRepository.save(order);
        
        events.publishEvent(new OrderPlacedEvent(order.getId(), productCode, quantity));
        
        return order;
    }
}
