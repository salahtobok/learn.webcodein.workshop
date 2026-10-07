package com.webcodein.workshop.saga;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderActivities {
    
    @ActivityMethod
    void processPayment(String accountId, double amount);

    @ActivityMethod
    void refundPayment(String accountId, double amount);

    @ActivityMethod
    void reserveInventory(String orderId);

    @ActivityMethod
    void releaseInventory(String orderId);
}
