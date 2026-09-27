package com.webcodein.workshop.dddmistakes.sales.application;
import com.webcodein.workshop.dddmistakes.sales.domain.OrderRepository;
import com.webcodein.workshop.dddmistakes.sales.domain.Order;
import com.webcodein.workshop.dddmistakes.sales.domain.OrderConfirmedEvent;
import org.springframework.context.ApplicationEventPublisher;
import java.util.UUID;
public class OrderCheckoutService {
    private final OrderRepository orderRepo;
    private final ApplicationEventPublisher eventPublisher;
    public OrderCheckoutService(OrderRepository orderRepo, ApplicationEventPublisher eventPublisher) {
        this.orderRepo = orderRepo;
        this.eventPublisher = eventPublisher;
    }
    public void checkout(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        order.confirm();
        orderRepo.save(order);
        eventPublisher.publishEvent(new OrderConfirmedEvent(order.getId(), order.getItems()));
    }
}