package com.webcodein.workshop.architecture.archunitenforceddd.presentation.controller;

import com.webcodein.workshop.architecture.archunitenforceddd.application.service.OrderService;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.customerId(),
                request.productCode(),
                request.price(),
                request.currency()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponse(order.getId(), order.getCustomerId(), order.getStatus().name()));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable UUID id) {
        Order order = orderService.payOrder(id);
        return ResponseEntity.ok(new OrderResponse(order.getId(), order.getCustomerId(), order.getStatus().name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return orderService.getOrder(id)
                .map(order -> ResponseEntity.ok(new OrderResponse(order.getId(), order.getCustomerId(), order.getStatus().name())))
                .orElse(ResponseEntity.notFound().build());
    }

    public record CreateOrderRequest(String customerId, String productCode, BigDecimal price, String currency) {}
    public record OrderResponse(UUID id, String customerId, String status) {}
}
