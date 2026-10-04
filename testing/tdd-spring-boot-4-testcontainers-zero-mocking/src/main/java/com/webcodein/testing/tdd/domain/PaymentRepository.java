package com.webcodein.testing.tdd.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReference(String reference);
}
