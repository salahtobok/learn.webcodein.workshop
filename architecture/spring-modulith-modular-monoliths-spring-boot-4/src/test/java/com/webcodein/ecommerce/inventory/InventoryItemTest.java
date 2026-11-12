package com.webcodein.ecommerce.inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventoryItem Entity Invariant Tests")
class InventoryItemTest {

    @Test
    @DisplayName("Should create item with valid productCode and stock")
    void shouldCreateItem() {
        InventoryItem item = new InventoryItem("TABLET-PRO", 20);
        assertEquals("TABLET-PRO", item.getProductCode());
        assertEquals(20, item.getStockQuantity());
    }

    @Test
    @DisplayName("Should reduce stock when sufficient quantity available")
    void shouldReduceStock() {
        InventoryItem item = new InventoryItem("TABLET-PRO", 10);
        item.reduceStock(4);
        assertEquals(6, item.getStockQuantity());
    }

    @Test
    @DisplayName("Should throw exception when reducing stock by amount greater than available")
    void shouldThrowWhenNotEnoughStock() {
        InventoryItem item = new InventoryItem("TABLET-PRO", 5);
        assertThrows(IllegalArgumentException.class, () -> item.reduceStock(6));
    }

    @Test
    @DisplayName("Should throw exception when reducing or adding non-positive amounts")
    void shouldThrowOnInvalidAmounts() {
        InventoryItem item = new InventoryItem("TABLET-PRO", 5);
        assertThrows(IllegalArgumentException.class, () -> item.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> item.reduceStock(-1));
        assertThrows(IllegalArgumentException.class, () -> item.addStock(0));
        assertThrows(IllegalArgumentException.class, () -> item.addStock(-3));
    }

    @Test
    @DisplayName("Should correctly increase stock via addStock")
    void shouldAddStock() {
        InventoryItem item = new InventoryItem("TABLET-PRO", 5);
        item.addStock(15);
        assertEquals(20, item.getStockQuantity());
    }
}
