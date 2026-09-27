package com.webcodein.workshop.dddmistakes.ecommerce.domain;
import java.math.BigDecimal;
import java.util.UUID;
public class Product {
    private UUID id;
    private BigDecimal price;
    public Product(UUID id, BigDecimal price) { this.id = id; this.price = price; }
    public UUID getId() { return id; }
    public BigDecimal getPrice() { return price; }
}