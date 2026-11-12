package com.webcodein.workshop.ddd.infrastructure.persistence;

import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderId;
import com.webcodein.workshop.ddd.domain.repository.OrderDomainRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderDomainRepository implements OrderDomainRepository {

    private final Map<OrderId, Order> database = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        database.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public void deleteById(OrderId id) {
        database.remove(id);
    }

    public void clear() {
        database.clear();
    }
}
