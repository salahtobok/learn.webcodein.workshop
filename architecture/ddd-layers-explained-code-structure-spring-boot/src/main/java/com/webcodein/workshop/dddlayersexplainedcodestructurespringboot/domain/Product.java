package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

public class Product {
    private final ProductId id;
    private ProductName name;
    private Money price;
    private int stockQuantity;

    public Product(ProductId id, ProductName name, Money price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Business method with invariant enforcement
    public void reduceStock(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new InsufficientStockException(this.id, quantity, this.stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    public boolean isInStock() {
        return this.stockQuantity > 0;
    }

    public ProductId getId() { return id; }
    public ProductName getName() { return name; }
    public Money getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
}
