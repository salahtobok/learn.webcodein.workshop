package com.webcodein.workshop.architecture.archunitenforceddd.domain.repository;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.DomainRepository;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(UUID id);
}
