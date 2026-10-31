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
}
