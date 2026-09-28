package com.webcodein.observability.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final RestClient restClient;

    public OrderController() {
        this.restClient = RestClient.create();
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable String id) {
        log.info("Received request for order ID: {}", id);
        
        // Simulating a call to another service to demonstrate distributed tracing
        String inventoryStatus = restClient.get()
                .uri("http://localhost:8080/api/inventory/" + id)
                .retrieve()
                .body(String.class);
        
        log.info("Inventory status retrieved: {}", inventoryStatus);
        return "Order " + id + " processed. Inventory status: " + inventoryStatus;
    }
}
