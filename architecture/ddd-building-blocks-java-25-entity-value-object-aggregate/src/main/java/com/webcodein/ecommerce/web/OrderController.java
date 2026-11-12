package com.webcodein.ecommerce.web;

import com.webcodein.ecommerce.domain.Money;
import com.webcodein.ecommerce.domain.Order;
import com.webcodein.ecommerce.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        return orderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
