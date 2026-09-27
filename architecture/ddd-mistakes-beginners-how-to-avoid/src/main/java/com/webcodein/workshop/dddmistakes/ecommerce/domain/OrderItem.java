package com.webcodein.workshop.dddmistakes.ecommerce.domain;
import java.math.BigDecimal;
import java.util.UUID;
public class OrderItem {
    private UUID productId;
    private BigDecimal price;
    private int quantity;
    public OrderItem(UUID productId, BigDecimal price, int quantity) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }
    public BigDecimal getSubtotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
}