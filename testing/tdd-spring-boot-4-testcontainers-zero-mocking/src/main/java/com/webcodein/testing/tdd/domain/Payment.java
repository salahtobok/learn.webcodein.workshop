package com.webcodein.testing.tdd.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private UUID id;
    private String reference;
    private BigDecimal amount;
    private String status;

    protected Payment() {
    }

    public Payment(UUID id, String reference, BigDecimal amount, String status) {
        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
