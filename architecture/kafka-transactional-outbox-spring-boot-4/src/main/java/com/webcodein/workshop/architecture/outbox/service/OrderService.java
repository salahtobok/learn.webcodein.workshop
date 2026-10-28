package com.webcodein.workshop.architecture.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.workshop.architecture.outbox.dto.OrderCreatedEvent;
import com.webcodein.workshop.architecture.outbox.dto.OrderRequest;
import com.webcodein.workshop.architecture.outbox.entity.Order;
import com.webcodein.workshop.architecture.outbox.entity.OrderStatus;
import com.webcodein.workshop.architecture.outbox.entity.OutboxEvent;
import com.webcodein.workshop.architecture.outbox.repository.OrderRepository;
import com.webcodein.workshop.architecture.outbox.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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
    public Order createOrder(OrderRequest request) {
        Instant now = Instant.now();

        // 1. Persist the business entity in the database
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setProductCode(request.productCode());
        order.setQuantity(request.quantity());
        order.setTotalAmount(request.totalAmount());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(now);

        Order savedOrder = orderRepository.save(order);

        // 2. Prepare event payload
        OrderCreatedEvent eventPayload = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getProductCode(),
                savedOrder.getQuantity(),
                savedOrder.getTotalAmount(),
                now
        );

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(eventPayload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order event payload", e);
        }

        // 3. Persist the outbox event in the same ACID database transaction
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("Order");
        outboxEvent.setAggregateId(savedOrder.getId().toString());
        outboxEvent.setEventType("OrderCreated");
        outboxEvent.setPayload(jsonPayload);
        outboxEvent.setCreatedAt(now);
        outboxEvent.setProcessed(false);

        outboxEventRepository.save(outboxEvent);

        return savedOrder;
    }
}
