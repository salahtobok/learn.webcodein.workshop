package com.webcodein.workshop.architecture.hexagonal.adapter.in.web;

import com.webcodein.workshop.architecture.hexagonal.application.port.in.PlaceOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
    }
    
    public record OrderRequest(String product, int quantity) {}

    @PostMapping
    public ResponseEntity<UUID> placeOrder(@RequestBody OrderRequest request) {
        Order order = new Order(UUID.randomUUID(), request.product(), request.quantity());
        UUID id = placeOrderUseCase.placeOrder(order);
        return ResponseEntity.ok(id);
    }
}
