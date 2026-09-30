package com.webcodein.cqrs.core;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_store")
public class EventStoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID aggregateId;
    private String eventType;
    private Instant timestamp;
    
    @Column(columnDefinition = "TEXT")
    private String payload;

    public EventStoreEntity() {}

    public EventStoreEntity(UUID aggregateId, String eventType, String payload, Instant timestamp) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public Instant getTimestamp() { return timestamp; }
}
