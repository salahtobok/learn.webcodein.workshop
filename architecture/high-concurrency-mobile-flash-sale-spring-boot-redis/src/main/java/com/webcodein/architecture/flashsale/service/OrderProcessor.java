package com.webcodein.architecture.flashsale.service;
import com.webcodein.architecture.flashsale.domain.Order;
import com.webcodein.architecture.flashsale.domain.OrderRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor {
    private final OrderRepository orderRepository;

    public OrderProcessor(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Async
    public void processOrder(String productId, String userId) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        orderRepository.save(new Order(productId, userId));
    }
}
