package com.webcodein.workshop.saga;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class OrderFulfillmentWorkflowImpl implements OrderFulfillmentWorkflow {

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .setRetryOptions(io.temporal.common.RetryOptions.newBuilder().setMaximumAttempts(1).build())
            .build();

    private final OrderActivities activities = Workflow.newActivityStub(OrderActivities.class, options);

    @Override
    public void fulfillOrder(String orderId, String customerId, double amount) {
        // Configure Saga options
        Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(false).build();
        Saga saga = new Saga(sagaOptions);

        try {
            // Step 1: Process Payment
            activities.processPayment(customerId, amount);
            saga.addCompensation(activities::refundPayment, customerId, amount);

            // Step 2: Reserve Inventory
            activities.reserveInventory(orderId);
            saga.addCompensation(activities::releaseInventory, orderId);

            // If all succeeds, the order is fulfilled
            System.out.println("Workflow completed successfully for order: " + orderId);

        } catch (Exception e) {
            System.out.println("Workflow failed for order " + orderId + ". Executing compensations...");
            // Execute all registered compensations in reverse order
            saga.compensate();
            throw Workflow.wrap(e);
        }
    }
}
