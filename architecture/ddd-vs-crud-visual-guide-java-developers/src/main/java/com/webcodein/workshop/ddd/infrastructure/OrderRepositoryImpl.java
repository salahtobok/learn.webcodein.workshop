package com.webcodein.workshop.ddd.infrastructure;

import com.webcodein.workshop.ddd.domain.Email;
import com.webcodein.workshop.ddd.domain.Money;
import com.webcodein.workshop.ddd.domain.Order;
import com.webcodein.workshop.ddd.domain.OrderRepository;
import com.webcodein.workshop.ddd.domain.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final SpringDataOrderRepository jpaRepo;

    public OrderRepositoryImpl(SpringDataOrderRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity;
        if (order.getId() != null) {
            entity = jpaRepo.findById(order.getId()).orElseGet(OrderJpaEntity::new);
        } else {
            entity = new OrderJpaEntity();
        }

        entity.setCustomerEmail(order.getCustomerEmail().value());
        entity.setTotalAmount(order.getTotalAmount().amount());
        entity.setCurrency(order.getTotalAmount().currency());
        entity.setStatus(order.getStatus().name());

        OrderJpaEntity saved = jpaRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepo.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }

    private Order toDomain(OrderJpaEntity entity) {
        Email email = new Email(entity.getCustomerEmail());
        Money money = Money.of(entity.getTotalAmount(), entity.getCurrency());
        OrderStatus status = OrderStatus.valueOf(entity.getStatus());
        return Order.reconstitute(entity.getId(), email, money, status);
    }
}
