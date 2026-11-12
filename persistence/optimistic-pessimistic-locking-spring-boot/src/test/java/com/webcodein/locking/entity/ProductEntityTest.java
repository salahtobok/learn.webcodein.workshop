package com.webcodein.locking.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    @DisplayName("Should create product and decrement stock successfully")
    void createAndDecrementStock() {
        Product product = new Product("Ergonomic Keyboard", 100);
        product.setId(1L);
        product.setVersion(0L);

        assertEquals("Ergonomic Keyboard", product.getName());
        assertEquals(100, product.getStock());
        assertEquals(0L, product.getVersion());

        product.decrementStock(15);
        assertEquals(85, product.getStock());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when decrement exceeds available stock")
    void rejectInsufficientStock() {
        Product product = new Product("Wireless Mouse", 5);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> product.decrementStock(10));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Should update product name and stock via setters")
    void updateFields() {
        Product product = new Product();
        product.setName("Gaming Headset");
        product.setStock(50);
        product.setVersion(2L);

        assertEquals("Gaming Headset", product.getName());
        assertEquals(50, product.getStock());
        assertEquals(2L, product.getVersion());
    }
}
