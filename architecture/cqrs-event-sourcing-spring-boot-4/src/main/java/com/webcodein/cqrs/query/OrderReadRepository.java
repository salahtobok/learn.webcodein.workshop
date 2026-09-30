package com.webcodein.cqrs.query;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderReadRepository extends JpaRepository<OrderReadModel, UUID> {}
