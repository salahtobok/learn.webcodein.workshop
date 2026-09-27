package com.webcodein.boundedcontext.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "catalog_products")
public class Product {
    @Id
    private UUID id;
    private String name;
    private String description;
    private double price;

    protected Product() {} // JPA requirement

    public Product(String name, String description, double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
}
