package com.webcodein.hikaricp.controller;

import com.webcodein.hikaricp.dto.OrderRequest;
import com.webcodein.hikaricp.entity.ProductOrder;
import com.webcodein.hikaricp.service.OrderProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessingService orderService;

    public OrderController(OrderProcessingService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ProductOrder> createOrder(@RequestBody OrderRequest request) {
        ProductOrder created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/slow")
    public ResponseEntity<Map<String, Object>> simulateSlowQuery(
            @RequestParam(defaultValue = "1.5") double delaySeconds) {
        orderService.processSlowOrder(delaySeconds);
        return ResponseEntity.ok(Map.of(
                "status", "COMPLETED",
                "simulatedDelaySeconds", delaySeconds
        ));
    }

    @GetMapping("/leak")
    public ResponseEntity<Map<String, String>> simulateConnectionLeak() {
        String message = orderService.simulateConnectionLeak();
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/leak/release")
    public ResponseEntity<Map<String, String>> releaseLeakedConnections() {
        orderService.releaseLeakedConnections();
        return ResponseEntity.ok(Map.of("message", "All simulated leaked connections released"));
    }
}
