package com.webcodein.architecture.flashsale.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private String userId;

    public Order() {}
    public Order(String productId, String userId) {
        this.productId = productId;
        this.userId = userId;
    }
    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public String getUserId() { return userId; }
}
