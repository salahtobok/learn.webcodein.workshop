package com.webcodein.workshop.architecture.outbox.controller;

import com.webcodein.workshop.architecture.outbox.dto.OrderRequest;
import com.webcodein.workshop.architecture.outbox.entity.Order;
import com.webcodein.workshop.architecture.outbox.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
