package com.webcodein.workshop.chaos;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam String product, @RequestParam int quantity) {
        Order order = orderService.placeOrder(product, quantity);
        if ("FAILED_NETWORK_ISSUE".equals(order.getStatus())) {
            return ResponseEntity.status(503).body(order);
        }
        return ResponseEntity.ok(order);
    }
}
