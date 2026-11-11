package com.webcodein.workshop.architecture.ddd.application;

import com.webcodein.workshop.architecture.ddd.domain.model.order.*;
import com.webcodein.workshop.architecture.ddd.domain.repository.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderApplicationService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public OrderDto createOrder(CreateOrderCommand command) {
        OrderId orderId = OrderId.generate();
        CustomerEmail email = new CustomerEmail(command.customerEmail());
        Order order = new Order(orderId, email);

        Order saved = orderRepository.save(order);
        publishEvents(order);
        return OrderDto.fromDomain(saved);
    }

    public OrderDto addItem(UUID orderId, AddItemCommand command) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        BookId bookId = new BookId(command.bookId());
        Quantity quantity = new Quantity(command.quantity());
        Money price = new Money(command.unitPrice(), command.currency());

        order.addLine(bookId, quantity, price);
        Order saved = orderRepository.save(order);
        publishEvents(order);
        return OrderDto.fromDomain(saved);
    }

    public OrderDto confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        order.confirm();
        Order saved = orderRepository.save(order);
        publishEvents(order);
        return OrderDto.fromDomain(saved);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
        return OrderDto.fromDomain(order);
    }

    private void publishEvents(Order order) {
        for (Object event : order.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
        order.clearDomainEvents();
    }
}
