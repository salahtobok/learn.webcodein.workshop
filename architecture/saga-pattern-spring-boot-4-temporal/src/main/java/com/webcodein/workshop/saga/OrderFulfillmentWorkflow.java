package com.webcodein.workshop.saga;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderFulfillmentWorkflow {
    
    @WorkflowMethod
    void fulfillOrder(String orderId, String customerId, double amount);
}
