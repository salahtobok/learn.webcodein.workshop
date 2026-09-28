package com.webcodein.architecture.clean.gateways;
import com.webcodein.architecture.clean.entities.Order;
import com.webcodein.architecture.clean.usecases.OrderGateway;
import org.springframework.stereotype.Repository;
@Repository
public class OrderRepository implements OrderGateway {
    @Override
    public Order save(Order order) { return order; }
}
