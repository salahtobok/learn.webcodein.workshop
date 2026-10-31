package com.webcodein.workshop.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderActivitiesImpl implements OrderActivities {

    private static final Logger log = LoggerFactory.getLogger(OrderActivitiesImpl.class);

    @Override
    public void processPayment(String accountId, double amount) {
        log.info("Processing payment for account {} for amount {}", accountId, amount);
        // Simulate external payment gateway call
    }

    @Override
    public void refundPayment(String accountId, double amount) {
        log.warn("Refunding payment for account {} for amount {}", accountId, amount);
        // Execute compensating transaction
    }

    @Override
    public void reserveInventory(String orderId) {
        log.info("Reserving inventory for order {}", orderId);
        // Simulate inventory failure for the workshop demo if orderId contains "fail"
        if (orderId != null && orderId.contains("fail")) {
            log.error("Inventory reservation failed for order {}: Out of stock!", orderId);
            throw new RuntimeException("Out of stock!");
        }
    }

    @Override
    public void releaseInventory(String orderId) {
        log.warn("Releasing reserved inventory for order {}", orderId);
        // Execute compensating transaction
    }
}
