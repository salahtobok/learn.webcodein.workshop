package com.webcodein.workshop.mcp;

import com.webcodein.workshop.mcp.model.InventoryItem;
import com.webcodein.workshop.mcp.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryServiceTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();
    }

    @Test
    @DisplayName("Should find existing inventory item by SKU")
    void shouldFindExistingInventoryItem() {
        Optional<InventoryItem> item = inventoryService.checkStock("SKU-MACBOOK-16");

        assertThat(item).isPresent();
        assertThat(item.get().name()).contains("MacBook Pro 16");
        assertThat(item.get().stockQuantity()).isGreaterThan(0);
        assertThat(item.get().inStock()).isTrue();
    }

    @Test
    @DisplayName("Should return empty optional for non-existing SKU")
    void shouldReturnEmptyForUnknownSku() {
        Optional<InventoryItem> item = inventoryService.checkStock("SKU-DOES-NOT-EXIST");

        assertThat(item).isEmpty();
    }

    @Test
    @DisplayName("Should reserve available stock successfully")
    void shouldReserveStockSuccessfully() {
        boolean reserved = inventoryService.reserveStock("SKU-MACBOOK-16", 2);

        assertThat(reserved).isTrue();
        InventoryItem updated = inventoryService.checkStock("SKU-MACBOOK-16").orElseThrow();
        assertThat(updated.stockQuantity()).isEqualTo(12);
    }
}
