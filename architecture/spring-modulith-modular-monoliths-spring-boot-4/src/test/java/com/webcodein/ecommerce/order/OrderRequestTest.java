package com.webcodein.ecommerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderRequest Record Validation Tests")
class OrderRequestTest {

    @Test
    @DisplayName("Should create valid OrderRequest")
    void shouldCreateValidRequest() {
        OrderRequest request = new OrderRequest("HEADPHONES-BT", 2);
        assertEquals("HEADPHONES-BT", request.productCode());
        assertEquals(2, request.quantity());
    }

    @Test
    @DisplayName("Should reject blank productCode")
    void shouldRejectBlankProductCode() {
        assertThrows(NullPointerException.class, () -> new OrderRequest(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new OrderRequest("   ", 1));
    }

    @Test
    @DisplayName("Should reject non-positive quantity")
    void shouldRejectNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new OrderRequest("HEADPHONES-BT", 0));
        assertThrows(IllegalArgumentException.class, () -> new OrderRequest("HEADPHONES-BT", -5));
    }
}
