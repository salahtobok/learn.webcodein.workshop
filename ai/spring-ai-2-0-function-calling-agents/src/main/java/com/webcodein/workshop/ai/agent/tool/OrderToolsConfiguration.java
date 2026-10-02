package com.webcodein.workshop.ai.agent.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Configuration
public class OrderToolsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OrderToolsConfiguration.class);
    
    // Simulating a database for orders
    private final Map<String, String> orderDatabase = new ConcurrentHashMap<>(Map.of(
            "ORD-1001", "Shipped - Tracking: AB123456",
            "ORD-1002", "Processing - Expected shipping in 2 days",
            "ORD-1003", "Delivered on 2026-10-01"
    ));

    public record OrderStatusRequest(String orderId) {}
    public record OrderStatusResponse(String orderId, String status, String message) {}

    @Bean
    @Description("Fetch the current shipping or processing status of an order using its order ID (e.g., ORD-XXXX).")
    public Function<OrderStatusRequest, OrderStatusResponse> getOrderStatus() {
        return request -> {
            log.info("Agent is checking status for order: {}", request.orderId());
            String status = orderDatabase.get(request.orderId());
            
            if (status == null) {
                return new OrderStatusResponse(request.orderId(), "NOT_FOUND", "No order found with this ID.");
            }
            return new OrderStatusResponse(request.orderId(), "FOUND", status);
        };
    }
    
    public record CancelOrderRequest(String orderId, String reason) {}
    public record CancelOrderResponse(String orderId, boolean success, String message) {}
    
    @Bean
    @Description("Cancel an existing order. Requires the order ID and a reason for cancellation. Only 'Processing' orders can be cancelled.")
    public Function<CancelOrderRequest, CancelOrderResponse> cancelOrder() {
        return request -> {
            log.info("Agent is attempting to cancel order: {} for reason: {}", request.orderId(), request.reason());
            String status = orderDatabase.get(request.orderId());
            
            if (status == null) {
                return new CancelOrderResponse(request.orderId(), false, "Cannot cancel. Order not found.");
            }
            if (status.startsWith("Shipped") || status.startsWith("Delivered")) {
                return new CancelOrderResponse(request.orderId(), false, "Cannot cancel. Order has already been shipped or delivered.");
            }
            
            orderDatabase.remove(request.orderId());
            return new CancelOrderResponse(request.orderId(), true, "Order successfully cancelled.");
        };
    }
}
