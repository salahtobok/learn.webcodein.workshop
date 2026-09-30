package com.webcodein.cqrs.core;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EventStoreRepository extends JpaRepository<EventStoreEntity, Long> {
    List<EventStoreEntity> findByAggregateIdOrderByTimestampAsc(UUID aggregateId);
}
