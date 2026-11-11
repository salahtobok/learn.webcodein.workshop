package com.webcodein.workshop.crud;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crud/orders")
public class CrudOrderController {

    private final OrderService orderService;

    public CrudOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(@RequestBody CreateOrderRequest request) {
        OrderEntity created = orderService.createOrder(request.customerEmail(), request.totalAmount());
        return ResponseEntity.created(URI.create("/api/crud/orders/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEntity> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> listOrders() {
        return ResponseEntity.ok(orderService.listOrders());
    }

    @PutMapping("/{id}/fulfill")
    public ResponseEntity<Map<String, String>> fulfillOrder(@PathVariable Long id) {
        orderService.fulfillOrder(id);
        return ResponseEntity.ok(Map.of("message", "Order fulfilled successfully"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(Map.of("message", "Order cancelled successfully"));
    }

    public record CreateOrderRequest(String customerEmail, BigDecimal totalAmount) {
    }
}
