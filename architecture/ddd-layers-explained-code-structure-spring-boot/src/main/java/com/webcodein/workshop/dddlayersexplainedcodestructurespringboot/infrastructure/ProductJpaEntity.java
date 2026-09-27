package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

// JPA Entity — lives in Infrastructure, NOT in Domain
@Entity
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    private UUID id;
    private String name;
    private BigDecimal price;
    private String currency;
    private int stockQuantity;

    // JPA needs a no-arg constructor
    protected ProductJpaEntity() {}

    public ProductJpaEntity(UUID id, String name, BigDecimal price, String currency, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.stockQuantity = stockQuantity;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
    public int getStockQuantity() { return stockQuantity; }
}
