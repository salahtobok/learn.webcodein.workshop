package com.webcodein.workshop.chaos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Order placeOrder(String product, int quantity) {
        log.info("Attempting to place order for {} (Quantity: {})", product, quantity);
        Order order = new Order(product, quantity, "PENDING");
        return orderRepository.save(order);
    }

    @Recover
    public Order fallbackPlaceOrder(Exception e, String product, int quantity) {
        log.error("All retry attempts failed. Order creation for {} aborted.", product);
        return new Order(product, quantity, "FAILED_NETWORK_ISSUE");
    }
}
