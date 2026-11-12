package com.webcodein.workshop.architecture.hexagonal.application.port.in;

import com.webcodein.workshop.architecture.hexagonal.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase {
    Optional<Order> getOrder(UUID id);
}
