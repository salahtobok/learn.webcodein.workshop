package com.webcodein.boundedcontext.order;

import com.webcodein.boundedcontext.order.dto.CreateOrderRequest;
import com.webcodein.boundedcontext.order.dto.OrderResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponseDto created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponseDto> getOrdersByCustomer(@PathVariable UUID customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDto> confirmOrder(@PathVariable UUID id) {
        OrderResponseDto confirmed = orderService.confirmOrder(id);
        return ResponseEntity.ok(confirmed);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable UUID id) {
        OrderResponseDto cancelled = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelled);
    }
}
