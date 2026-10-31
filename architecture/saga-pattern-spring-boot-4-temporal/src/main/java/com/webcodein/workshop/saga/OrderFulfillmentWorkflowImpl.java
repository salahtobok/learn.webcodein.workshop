package com.webcodein.workshop.saga;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class OrderFulfillmentWorkflowImpl implements OrderFulfillmentWorkflow {

    private static final Logger log = Workflow.getLogger(OrderFulfillmentWorkflowImpl.class);

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
            .build();

    private final OrderActivities activities = Workflow.newActivityStub(OrderActivities.class, options);

    @Override
    public void fulfillOrder(String orderId, String customerId, double amount) {
        // Configure Saga options: sequential reverse-order compensation
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);

        try {
            log.info("Starting Order Fulfillment Saga for order: {}, customer: {}", orderId, customerId);

            // Step 1: Process Payment
            activities.processPayment(customerId, amount);
            // Register compensation immediately after forward action succeeds
            saga.addCompensation(activities::refundPayment, customerId, amount);

            // Step 2: Reserve Inventory
            activities.reserveInventory(orderId);
            saga.addCompensation(activities::releaseInventory, orderId);

            // Forward actions succeeded - transaction complete
            log.info("Order Fulfillment Saga completed successfully for order: {}", orderId);

        } catch (Exception e) {
            log.error("Order Fulfillment Saga failed for order: {}. Executing compensating transactions...", orderId, e);
            // Execute all registered compensations in reverse order
            saga.compensate();
            throw Workflow.wrap(e);
        }
    }
}
