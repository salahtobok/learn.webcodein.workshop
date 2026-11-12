package com.webcodein.workshop.dddmistakes.ecommerce.api.controller;

import com.webcodein.workshop.dddmistakes.ecommerce.domain.Order;
import com.webcodein.workshop.dddmistakes.ecommerce.domain.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class OrderController {
    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/orders/{id}")
    public OrderResponseDTO getOrder(@PathVariable UUID id) {
        Order order = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
        return new OrderResponseDTO(
            order.getId().toString(),
            order.getTotalAmount().toPlainString(),
            order.getStatus().name()
        );
    }
}
