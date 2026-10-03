package com.webcodein.legacy.util;

import com.webcodein.legacy.order.OrderService;

public class LegacyGlobalHelper {

    // LEGACY VIOLATION: A generic util class depending on a specific domain service.
    public static void printOrder(OrderService orderService) {
        System.out.println(orderService.createOrder());
    }
}
