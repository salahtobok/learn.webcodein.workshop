package com.webcodein.workshop.architecture.hexagonal.application.port.out;

import com.webcodein.workshop.architecture.hexagonal.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    void save(Order order);
    Optional<Order> findById(UUID id);
}
