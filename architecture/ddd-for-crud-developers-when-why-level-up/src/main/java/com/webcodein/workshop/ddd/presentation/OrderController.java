package com.webcodein.workshop.ddd.presentation;

import com.webcodein.workshop.ddd.application.OrderApplicationService;
import com.webcodein.workshop.ddd.domain.model.CustomerId;
import com.webcodein.workshop.ddd.domain.model.Money;
import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderId;
import com.webcodein.workshop.ddd.presentation.dto.ApplyDiscountRequest;
import com.webcodein.workshop.ddd.presentation.dto.CreateOrderRequest;
import com.webcodein.workshop.ddd.presentation.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderService;

    public OrderController(OrderApplicationService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderId orderId = orderService.createOrder(
                CustomerId.of(request.customerId()),
                request.currency(),
                request.productCode(),
                Money.of(request.unitPrice(), request.currency()),
                request.quantity()
        );
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.created(URI.create("/api/orders/" + orderId.value()))
                .body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(OrderId.of(id));
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping("/{id}/discount")
    public ResponseEntity<OrderResponse> applyDiscount(
            @PathVariable UUID id,
            @Valid @RequestBody ApplyDiscountRequest request
    ) {
        orderService.applyDiscount(OrderId.of(id), Money.of(request.amount(), request.currency()));
        Order order = orderService.getOrder(OrderId.of(id));
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable UUID id) {
        orderService.shipOrder(OrderId.of(id));
        Order order = orderService.getOrder(OrderId.of(id));
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NoSuchElementException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException ex) {
        return Map.of("error", ex.getMessage());
    }
}
