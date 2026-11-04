package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("Should create product with valid domain invariants")
    void shouldCreateProductWithValidInvariants() {
        ProductId id = ProductId.generate();
        ProductName name = new ProductName("Gaming Laptop");
        Money price = new Money(new BigDecimal("1299.99"), "USD");

        Product product = new Product(id, name, price, 15);

        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStockQuantity()).isEqualTo(15);
        assertThat(product.isInStock()).isTrue();
    }

    @Test
    @DisplayName("Should reduce stock successfully when sufficient inventory exists")
    void shouldReduceStockSuccessfully() {
        Product product = new Product(
            ProductId.generate(),
            new ProductName("Mechanical Keyboard"),
            new Money(new BigDecimal("149.00"), "USD"),
            10
        );

        product.reduceStock(4);

        assertThat(product.getStockQuantity()).isEqualTo(6);
        assertThat(product.isInStock()).isTrue();
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when reduction exceeds available stock")
    void shouldThrowWhenReducingMoreThanAvailableStock() {
        ProductId id = ProductId.generate();
        Product product = new Product(
            id,
            new ProductName("Wireless Mouse"),
            new Money(new BigDecimal("79.99"), "USD"),
            3
        );

        assertThatThrownBy(() -> product.reduceStock(5))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining(id.value().toString());
    }

    @Test
    @DisplayName("Should report not in stock when stock quantity reaches zero")
    void shouldReportNotInStockWhenZero() {
        Product product = new Product(
            ProductId.generate(),
            new ProductName("USB-C Cable"),
            new Money(new BigDecimal("19.99"), "USD"),
            1
        );

        product.reduceStock(1);

        assertThat(product.getStockQuantity()).isZero();
        assertThat(product.isInStock()).isFalse();
    }
}
