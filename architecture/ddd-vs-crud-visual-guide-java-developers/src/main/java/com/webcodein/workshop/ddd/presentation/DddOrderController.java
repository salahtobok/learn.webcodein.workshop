package com.webcodein.workshop.ddd.presentation;

import com.webcodein.workshop.ddd.application.CreateOrderCommand;
import com.webcodein.workshop.ddd.application.OrderApplicationService;
import com.webcodein.workshop.ddd.application.OrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ddd/orders")
public class DddOrderController {

    private final OrderApplicationService appService;

    public DddOrderController(OrderApplicationService appService) {
        this.appService = appService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody CreateOrderCommand command) {
        Long id = appService.createOrder(command);
        return ResponseEntity.created(URI.create("/api/ddd/orders/" + id))
                .body(Map.of("id", id, "message", "Order created in DDD domain successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(appService.getOrder(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> listOrders() {
        return ResponseEntity.ok(appService.listOrders());
    }

    @PutMapping("/{id}/fulfill")
    public ResponseEntity<Map<String, String>> fulfillOrder(@PathVariable Long id) {
        appService.fulfillOrder(id);
        return ResponseEntity.ok(Map.of("message", "Order fulfilled via Domain Aggregate"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id,
                                                           @RequestBody CancelRequest request) {
        appService.cancelOrder(id, request.reason());
        return ResponseEntity.ok(Map.of("message", "Order cancelled via Domain Aggregate"));
    }

    public record CancelRequest(String reason) {
        public CancelRequest() {
            this("Customer request");
        }
    }
}
