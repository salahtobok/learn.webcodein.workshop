package com.webcodein.ecommerce.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    private Money price;

    private int quantity;

    protected OrderItem() {}

    public OrderItem(String productId, Money price, int quantity) {
        this.productId = Objects.requireNonNull(productId, "productId cannot be null");
        this.price = Objects.requireNonNull(price, "price cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

    public OrderItem(Long id, String productId, Money price, int quantity) {
        this(productId, price, quantity);
        this.id = id;
    }

    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public Money getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
