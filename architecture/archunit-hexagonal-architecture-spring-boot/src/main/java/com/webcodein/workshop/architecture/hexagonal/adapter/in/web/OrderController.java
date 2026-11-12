package com.webcodein.workshop.architecture.hexagonal.adapter.in.web;

import com.webcodein.workshop.architecture.hexagonal.application.port.in.GetOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.application.port.in.PlaceOrderUseCase;
import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    public record OrderRequest(String product, int quantity) {}

    public record OrderResponse(UUID id, String product, int quantity) {
        public static OrderResponse from(Order order) {
            return new OrderResponse(order.getId(), order.getProduct(), order.getQuantity());
        }
    }

    @PostMapping
    public ResponseEntity<UUID> placeOrder(@RequestBody OrderRequest request) {
        Order order = new Order(UUID.randomUUID(), request.product(), request.quantity());
        UUID id = placeOrderUseCase.placeOrder(order);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return getOrderUseCase.getOrder(id)
                .map(order -> ResponseEntity.ok(OrderResponse.from(order)))
                .orElse(ResponseEntity.notFound().build());
    }
}
