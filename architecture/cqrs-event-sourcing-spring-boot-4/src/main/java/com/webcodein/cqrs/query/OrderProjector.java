package com.webcodein.cqrs.query;

import com.webcodein.cqrs.events.DomainEvent;
import com.webcodein.cqrs.events.OrderConfirmedEvent;
import com.webcodein.cqrs.events.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProjector {

    private final OrderReadRepository repository;

    public OrderProjector(OrderReadRepository repository) {
        this.repository = repository;
    }

    @EventListener
    @Transactional
    public void on(DomainEvent event) {
        // Using Java 25 Pattern Matching for switch
        switch (event) {
            case OrderCreatedEvent created -> {
                OrderReadModel readModel = new OrderReadModel(
                        created.aggregateId(),
                        created.customerId(),
                        created.amount(),
                        "PENDING"
                );
                repository.save(readModel);
            }
            case OrderConfirmedEvent confirmed -> {
                repository.findById(confirmed.aggregateId()).ifPresent(readModel -> {
                    readModel.setStatus("CONFIRMED");
                    repository.save(readModel);
                });
            }
        }
    }
}
