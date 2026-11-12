package com.webcodein.legacy.util;

import com.webcodein.legacy.order.OrderService;
import com.webcodein.legacy.user.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LegacyGlobalHelperTest {

    @Test
    @DisplayName("Verify LegacyGlobalHelper prints without throwing exceptions")
    void shouldPrintOrder() {
        UserController userController = new UserController();
        OrderService orderService = new OrderService(userController);

        assertDoesNotThrow(() -> LegacyGlobalHelper.printOrder(orderService));
    }
}
