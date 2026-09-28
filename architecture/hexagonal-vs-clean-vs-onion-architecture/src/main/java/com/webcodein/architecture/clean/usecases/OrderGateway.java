package com.webcodein.architecture.clean.usecases;
import com.webcodein.architecture.clean.entities.Order;
public interface OrderGateway {
    Order save(Order order);
}
