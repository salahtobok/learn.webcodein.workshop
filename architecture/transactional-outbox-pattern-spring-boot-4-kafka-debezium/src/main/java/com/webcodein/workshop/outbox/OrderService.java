package com.webcodein.workshop.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order placeOrder(String product, BigDecimal price) {
        // 1. Save the Business Entity
        Order order = new Order(product, price);
        order = orderRepository.save(order);

        // 2. Create the JSON payload
        String payload;
        try {
            payload = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order", e);
        }

        // 3. Save the Outbox Event in the same transaction
        OutboxEvent event = new OutboxEvent(
                "Order",
                order.getId().toString(),
                "OrderCreated",
                payload
        );
        outboxEventRepository.save(event);

        return order;
    }
}
