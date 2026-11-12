package com.webcodein.workshop.dddmistakes.logistics.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderFulfillmentService Logistics Tests")
class OrderFulfillmentServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private LogisticsProvider logisticsProvider;

    @InjectMocks
    private OrderFulfillmentService fulfillmentService;

    @Test
    @DisplayName("Should allocate inventory and schedule pickup when order is ready for fulfillment")
    void shouldDispatchOrderSuccessfully() {
        Order order = mock(Order.class);
        when(order.isReadyForFulfillment()).thenReturn(true);
        when(order.getShippingAddress()).thenReturn(new Address());
        when(order.getItems()).thenReturn(Collections.emptyList());

        Warehouse warehouse = mock(Warehouse.class);
        DeliveryManifest manifest = mock(DeliveryManifest.class);
        when(warehouseRepository.findNearestTo(any())).thenReturn(warehouse);
        when(warehouse.allocateInventory(any())).thenReturn(manifest);

        fulfillmentService.dispatchOrderForDelivery(order);

        verify(logisticsProvider).schedulePickup(manifest);
        verify(order).markAsDispatched();
    }
}
