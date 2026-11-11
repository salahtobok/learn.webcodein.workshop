package com.webcodein.workshop.architecture.ddd.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_lines")
public class OrderLineJpaEntity {

    @Id
    private UUID id;

    private String bookId;

    private int quantity;

    private BigDecimal unitPriceAmount;

    private String currency;

    protected OrderLineJpaEntity() {}

    public OrderLineJpaEntity(UUID id, String bookId, int quantity, BigDecimal unitPriceAmount, String currency) {
        this.id = id;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.currency = currency;
    }

    public UUID getId() { return id; }
    public String getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPriceAmount() { return unitPriceAmount; }
    public String getCurrency() { return currency; }
}
