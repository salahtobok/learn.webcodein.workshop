package com.webcodein.cqrs.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.cqrs.events.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventStorePublisher {

    private final EventStoreRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public EventStorePublisher(EventStoreRepository repository, ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    public void saveAndPublish(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            EventStoreEntity entity = new EventStoreEntity(
                    event.aggregateId(),
                    event.getClass().getSimpleName(),
                    payload,
                    event.timestamp()
            );
            repository.save(entity);
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
