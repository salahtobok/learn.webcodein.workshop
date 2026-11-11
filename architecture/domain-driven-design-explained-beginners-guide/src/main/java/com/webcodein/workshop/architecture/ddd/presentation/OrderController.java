package com.webcodein.workshop.architecture.ddd.presentation;

import com.webcodein.workshop.architecture.ddd.application.AddItemCommand;
import com.webcodein.workshop.architecture.ddd.application.CreateOrderCommand;
import com.webcodein.workshop.architecture.ddd.application.OrderApplicationService;
import com.webcodein.workshop.architecture.ddd.application.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderCommand command) {
        OrderDto created = orderApplicationService.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderDto> addItem(
            @PathVariable UUID id,
            @RequestBody AddItemCommand command
    ) {
        OrderDto updated = orderApplicationService.addItem(id, command);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable UUID id) {
        OrderDto confirmed = orderApplicationService.confirmOrder(id);
        return ResponseEntity.ok(confirmed);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        OrderDto order = orderApplicationService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
