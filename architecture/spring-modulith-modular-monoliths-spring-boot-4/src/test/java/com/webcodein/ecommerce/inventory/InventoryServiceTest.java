package com.webcodein.ecommerce.inventory;

import com.webcodein.ecommerce.order.OrderPlacedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Unit Tests")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Should reduce stock when OrderPlacedEvent is received for existing product")
    void shouldReduceStockOnOrderPlacedEvent() {
        InventoryItem item = new InventoryItem("LAPTOP-X", 10);
        when(inventoryRepository.findById("LAPTOP-X")).thenReturn(Optional.of(item));

        OrderPlacedEvent event = new OrderPlacedEvent(1L, "LAPTOP-X", 3);
        inventoryService.on(event);

        assertThat(item.getStockQuantity()).isEqualTo(7);
        verify(inventoryRepository, times(1)).save(item);
    }

    @Test
    @DisplayName("Should handle missing product gracefully without throwing exception")
    void shouldHandleMissingProductGracefully() {
        when(inventoryRepository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        OrderPlacedEvent event = new OrderPlacedEvent(2L, "NON-EXISTENT", 1);
        inventoryService.on(event);

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return stock quantity when product exists")
    void shouldReturnStockWhenProductExists() {
        InventoryItem item = new InventoryItem("DESK-CHAIR", 15);
        when(inventoryRepository.findById("DESK-CHAIR")).thenReturn(Optional.of(item));

        Optional<Integer> stock = inventoryService.getStock("DESK-CHAIR");
        assertThat(stock).isPresent().contains(15);
    }

    @Test
    @DisplayName("Should restock existing product by adding quantity")
    void shouldRestockExistingProduct() {
        InventoryItem item = new InventoryItem("MONITOR-27", 5);
        when(inventoryRepository.findById("MONITOR-27")).thenReturn(Optional.of(item));

        inventoryService.restock("MONITOR-27", 10);

        assertThat(item.getStockQuantity()).isEqualTo(15);
        verify(inventoryRepository).save(item);
    }
}
