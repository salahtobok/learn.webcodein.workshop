package com.webcodein.architecture.clean.usecases;
import com.webcodein.architecture.clean.entities.Order;
import org.springframework.stereotype.Service;
@Service
public class CreateOrderInteractor implements CreateOrderUseCase {
    private final OrderGateway orderGateway;
    public CreateOrderInteractor(OrderGateway orderGateway) { this.orderGateway = orderGateway; }
    @Override
    public Order execute(Order order) { return orderGateway.save(order); }
}
