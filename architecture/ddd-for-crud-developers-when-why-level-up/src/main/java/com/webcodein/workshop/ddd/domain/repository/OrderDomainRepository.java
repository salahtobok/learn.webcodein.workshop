package com.webcodein.workshop.ddd.domain.repository;

import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderId;

import java.util.Optional;

public interface OrderDomainRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    void deleteById(OrderId id);
}
