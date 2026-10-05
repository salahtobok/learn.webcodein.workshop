package com.webcodein.ecommerce.web;

import com.webcodein.ecommerce.domain.Money;
import com.webcodein.ecommerce.domain.Order;
import com.webcodein.ecommerce.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestParam String customerId) {
        Order order = new Order(customerId);
        return orderRepository.save(order);
    }

    @PostMapping("/{orderId}/items")
    public Order addItem(@PathVariable String orderId, 
                         @RequestParam String productId,
                         @RequestParam BigDecimal price,
                         @RequestParam String currency,
                         @RequestParam int qty) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        order.addItem(productId, Money.of(price, currency), qty);
        return orderRepository.save(order);
    }
}
