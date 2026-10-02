package com.webcodein.workshop.architecture.hexagonal.application.port.in;

import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import java.util.UUID;

public interface PlaceOrderUseCase {
    UUID placeOrder(Order order);
}
