package com.webcodein.architecture.clean.controllers;
import com.webcodein.architecture.clean.entities.Order;
import com.webcodein.architecture.clean.usecases.CreateOrderUseCase;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/clean/orders")
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    public OrderController(CreateOrderUseCase createOrderUseCase) { this.createOrderUseCase = createOrderUseCase; }
    @PostMapping
    public Order createOrder(@RequestBody Order order) { return createOrderUseCase.execute(order); }
}
