package com.webcodein.ecommerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderPlacedEvent Record Tests")
class OrderPlacedEventTest {

    @Test
    @DisplayName("Should create immutable event record with expected accessors")
    void shouldCreateEvent() {
        OrderPlacedEvent event = new OrderPlacedEvent(101L, "SMARTPHONE-Z", 1);
        assertEquals(101L, event.orderId());
        assertEquals("SMARTPHONE-Z", event.productCode());
        assertEquals(1, event.quantity());
    }
}
