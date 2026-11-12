package com.webcodein.workshop.dddmistakes.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Model Tests")
class ProductTest {

    @Test
    @DisplayName("Should expose ID and price correctly")
    void shouldExposeProductProperties() {
        UUID id = UUID.randomUUID();
        BigDecimal price = new BigDecimal("99.95");
        Product product = new Product(id, price);

        assertEquals(id, product.getId());
        assertEquals(price, product.getPrice());
    }
}
