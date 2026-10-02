package com.webcodein.workshop.architecture.hexagonal.adapter.out.persistence;

import com.webcodein.workshop.architecture.hexagonal.application.port.out.OrderRepositoryPort;
import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public void save(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(order.getId(), order.getProduct(), order.getQuantity());
        orderJpaRepository.save(entity);
    }
}
