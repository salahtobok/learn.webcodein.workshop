package com.webcodein.workshop.architecture.hexagonal.application.port.out;

import com.webcodein.workshop.architecture.hexagonal.domain.Order;

public interface OrderRepositoryPort {
    void save(Order order);
}
