package com.webcodein.workshop.architecture.outbox.repository;

import com.webcodein.workshop.architecture.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop10ByProcessedFalseOrderByCreatedAtAsc();
}
