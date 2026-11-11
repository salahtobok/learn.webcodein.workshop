package com.webcodein.workshop.architecture.ddd.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    private UUID id;

    private String customerEmail;

    private String status;

    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderLineJpaEntity> lines = new ArrayList<>();

    protected OrderJpaEntity() {}

    public OrderJpaEntity(UUID id, String customerEmail, String status, Instant createdAt) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderLineJpaEntity> getLines() { return lines; }

    public void setStatus(String status) { this.status = status; }
    public void setLines(List<OrderLineJpaEntity> lines) { this.lines = lines; }
}
