package com.webcodein.workshop.architecture.ddd.infrastructure.persistence;

import com.webcodein.workshop.architecture.ddd.domain.model.order.*;
import com.webcodein.workshop.architecture.ddd.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = toJpaEntity(order);
        OrderJpaEntity saved = springDataOrderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return springDataOrderRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(OrderId id) {
        springDataOrderRepository.deleteById(id.value());
    }

    private OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getId().value(),
                order.getCustomerEmail().value(),
                order.getStatus().name(),
                order.getCreatedAt()
        );

        List<OrderLineJpaEntity> lines = order.getLines().stream()
                .map(l -> new OrderLineJpaEntity(
                        l.getId(),
                        l.getBookId().value(),
                        l.getQuantity().value(),
                        l.getUnitPrice().amount(),
                        l.getUnitPrice().currency()
                ))
                .collect(Collectors.toList());

        entity.setLines(lines);
        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderLine> domainLines = new ArrayList<>();
        if (entity.getLines() != null) {
            for (OrderLineJpaEntity line : entity.getLines()) {
                domainLines.add(OrderLine.reconstitute(
                        line.getId(),
                        new BookId(line.getBookId()),
                        new Quantity(line.getQuantity()),
                        new Money(line.getUnitPriceAmount(), line.getCurrency())
                ));
            }
        }

        return Order.reconstitute(
                new OrderId(entity.getId()),
                new CustomerEmail(entity.getCustomerEmail()),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                domainLines
        );
    }
}
