package com.webcodein.cqrs.command;

import com.webcodein.cqrs.core.EventStorePublisher;
import com.webcodein.cqrs.events.OrderConfirmedEvent;
import com.webcodein.cqrs.events.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderCommandService {

    private final EventStorePublisher eventPublisher;

    public OrderCommandService(EventStorePublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID createOrder(String customerId, double amount) {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId, amount, Instant.now());
        eventPublisher.saveAndPublish(event);
        return orderId;
    }

    @Transactional
    public void confirmOrder(UUID orderId) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(orderId, Instant.now());
        eventPublisher.saveAndPublish(event);
    }
}
