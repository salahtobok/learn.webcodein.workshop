package com.webcodein.workshop.ai.agent.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("orderTools")
public class OrderTools {
    private static final Logger log = LoggerFactory.getLogger(OrderTools.class);
    
    private final Map<String, String> orderDatabase = new ConcurrentHashMap<>(Map.of(
            "ORD-1001", "Shipped - Tracking: AB123456",
            "ORD-1002", "Processing - Expected shipping in 2 days",
            "ORD-1003", "Delivered on 2026-10-01"
    ));

    public record OrderStatusResponse(String orderId, String status, String message) {}

    @Tool(description = "Fetch the current shipping or processing status of an order using its order ID (e.g., ORD-XXXX).")
    public OrderStatusResponse getOrderStatus(String orderId) {
        log.info("Agent is checking status for order: {}", orderId);
        String status = orderDatabase.get(orderId);
        if (status == null) {
            return new OrderStatusResponse(orderId, "NOT_FOUND", "No order found with this ID.");
        }
        return new OrderStatusResponse(orderId, "FOUND", status);
    }
    
    public record CancelOrderResponse(String orderId, boolean success, String message) {}
    
    @Tool(description = "Cancel an existing order. Requires the order ID and a reason for cancellation. Only 'Processing' orders can be cancelled.")
    public CancelOrderResponse cancelOrder(String orderId, String reason) {
        log.info("Agent is attempting to cancel order: {} for reason: {}", orderId, reason);
        String status = orderDatabase.get(orderId);
        if (status == null) {
            return new CancelOrderResponse(orderId, false, "Cannot cancel. Order not found.");
        }
        if (status.startsWith("Shipped") || status.startsWith("Delivered")) {
            return new CancelOrderResponse(orderId, false, "Cannot cancel. Order has already been shipped or delivered.");
        }
        orderDatabase.remove(orderId);
        return new CancelOrderResponse(orderId, true, "Order successfully cancelled.");
    }
}
