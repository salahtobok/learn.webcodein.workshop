package com.webcodein.boundedcontext.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "catalog_products")
public class Product {

    @Id
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private double price;

    protected Product() {} // JPA requirement

    public Product(String sku, String name, String description, double price) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Product SKU cannot be null or blank");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.name = Objects.requireNonNull(name, "Product name cannot be null");
        this.description = description;
        this.price = price;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }

    public void updatePrice(double newPrice) {
        if (newPrice < 0.0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        this.price = newPrice;
    }
}
