package com.webcodein.workshop.architecture.archunitenforceddd.infrastructure.persistence;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private final EntityManager entityManager;

    public JpaOrderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Order order) {
        entityManager.persist(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Order.class, id));
    }
}
