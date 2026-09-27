package com.webcodein.workshop.dddmistakes.ecommerce.domain;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class OrderTest {
    @Test
    void cannotCancelShippedOrder() {
        Order order = new Order(UUID.randomUUID(), "CUST-1");
        order.markAsShipped(); 
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.cancel();
        });
        assertEquals("Order has already shipped and cannot be canceled", exception.getMessage());
    }
}