package com.webcodein.observability;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        ObservationRegistry registry = ObservationRegistry.create();
        orderService = new OrderService(registry);
    }

    @Test
    @DisplayName("processOrder successfully returns processed order string")
    void processOrderSuccess() {
        String result = orderService.processOrder("ord-999");
        assertEquals("Processed ord-999", result);
    }

    @Test
    @DisplayName("processOrder with fail id throws RuntimeException")
    void processOrderFailure() {
        assertThrows(RuntimeException.class, () -> orderService.processOrder("fail"));
    }
}
