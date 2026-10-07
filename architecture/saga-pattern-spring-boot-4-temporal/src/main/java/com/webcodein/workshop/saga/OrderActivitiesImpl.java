package com.webcodein.workshop.saga;

public class OrderActivitiesImpl implements OrderActivities {

    @Override
    public void processPayment(String accountId, double amount) {
        System.out.println("Processing payment for account " + accountId + " for amount " + amount);
        // Simulate external API call
    }

    @Override
    public void refundPayment(String accountId, double amount) {
        System.out.println("Refunding payment for account " + accountId + " for amount " + amount);
    }

    @Override
    public void reserveInventory(String orderId) {
        System.out.println("Reserving inventory for order " + orderId);
        // Simulate inventory failure for the workshop demo if orderId contains "fail"
        if (orderId.contains("fail")) {
            throw new RuntimeException("Out of stock!");
        }
    }

    @Override
    public void releaseInventory(String orderId) {
        System.out.println("Releasing inventory for order " + orderId);
    }
}
