package com.webcodein.hikaricp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProductOrderTest {

    @Test
    @DisplayName("Should create valid product order with correct properties")
    void shouldCreateProductOrder() {
        Instant now = Instant.now();
        ProductOrder order = new ProductOrder("John Doe", "SKU-999", new BigDecimal("199.99"), "CONFIRMED", now);

        assertThat(order.getCustomerName()).isEqualTo("John Doe");
        assertThat(order.getProductCode()).isEqualTo("SKU-999");
        assertThat(order.getAmount()).isEqualTo(new BigDecimal("199.99"));
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should allow mutation of product order fields via setters")
    void shouldAllowFieldMutation() {
        ProductOrder order = new ProductOrder();
        order.setId(101L);
        order.setCustomerName("Jane Smith");
        order.setProductCode("SKU-888");
        order.setAmount(new BigDecimal("49.50"));
        order.setStatus("PROCESSING");
        Instant now = Instant.now();
        order.setCreatedAt(now);

        assertThat(order.getId()).isEqualTo(101L);
        assertThat(order.getCustomerName()).isEqualTo("Jane Smith");
        assertThat(order.getProductCode()).isEqualTo("SKU-888");
        assertThat(order.getAmount()).isEqualTo(new BigDecimal("49.50"));
        assertThat(order.getStatus()).isEqualTo("PROCESSING");
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }
}
