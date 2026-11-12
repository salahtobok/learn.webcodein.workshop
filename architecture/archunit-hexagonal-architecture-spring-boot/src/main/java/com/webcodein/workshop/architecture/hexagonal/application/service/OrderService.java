package com.webcodein.workshop.architecture.hexagonal.application.service;

import com.webcodein.workshop.architecture.hexagonal.application.port.in.GetOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.application.port.in.PlaceOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.application.port.out.OrderRepositoryPort;
import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService implements PlaceOrderUseCase, GetOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public OrderService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public UUID placeOrder(Order order) {
        orderRepositoryPort.save(order);
        return order.getId();
    }

    @Override
    public Optional<Order> getOrder(UUID id) {
        return orderRepositoryPort.findById(id);
    }
}
