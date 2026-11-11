package com.webcodein.observability.controller;

import com.webcodein.observability.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam String id) {
        log.info("Received request to create order: {}", id);
        try {
            String result = orderService.processOrder(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error creating order: {}", id, e);
            return ResponseEntity.internalServerError().body("Failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrder(@PathVariable String id) {
        log.info("Received request for order ID: {}", id);
        return ResponseEntity.ok("Order " + id + " details retrieved with trace context");
    }
}
