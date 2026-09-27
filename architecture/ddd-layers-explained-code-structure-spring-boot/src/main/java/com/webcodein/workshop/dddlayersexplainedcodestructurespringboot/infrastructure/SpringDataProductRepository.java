package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Spring Data interface — only used inside Infrastructure
public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {}
