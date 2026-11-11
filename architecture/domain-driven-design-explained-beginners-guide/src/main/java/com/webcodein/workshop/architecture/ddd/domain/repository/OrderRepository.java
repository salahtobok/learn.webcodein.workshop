package com.webcodein.workshop.architecture.ddd.domain.repository;

import com.webcodein.workshop.architecture.ddd.domain.model.order.Order;
import com.webcodein.workshop.architecture.ddd.domain.model.order.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findAll();
    void deleteById(OrderId id);
}
