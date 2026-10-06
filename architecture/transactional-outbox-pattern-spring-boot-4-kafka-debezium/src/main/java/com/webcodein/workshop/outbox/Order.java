package com.webcodein.workshop.outbox;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;
    private BigDecimal price;

    public Order() {}

    public Order(String product, BigDecimal price) {
        this.product = product;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getProduct() { return product; }
    public BigDecimal getPrice() { return price; }
}
