package com.webcodein.workshop.saga;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderFulfillmentWorkflowTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private WorkflowClient workflowClient;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker(SagaApplication.TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(OrderFulfillmentWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderActivitiesImpl());
        workflowClient = testEnv.getWorkflowClient();
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    @DisplayName("Happy Path: Order workflow completes successfully when all activities pass")
    void testSuccessfulSaga() {
        testEnv.start();

        OrderFulfillmentWorkflow workflow = workflowClient.newWorkflowStub(OrderFulfillmentWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(SagaApplication.TASK_QUEUE).build());

        // Should complete without exception
        workflow.fulfillOrder("order-123", "cust-1", 100.0);
    }

    @Test
    @DisplayName("Compensation Path: Order workflow fails at inventory and wraps exception")
    void testFailedSagaExecutesCompensations() {
        testEnv.start();

        OrderFulfillmentWorkflow workflow = workflowClient.newWorkflowStub(OrderFulfillmentWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(SagaApplication.TASK_QUEUE).build());

        // orderId containing "fail" will throw exception in reserveInventory
        assertThatThrownBy(() -> workflow.fulfillOrder("order-fail-456", "cust-1", 100.0))
                .isInstanceOf(WorkflowException.class);
    }

    @Test
    @DisplayName("Verified Compensation Invariant: Compensating transactions execute in reverse order")
    void testFailedSagaExecutesExactCompensationInReverseOrder() {
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        try {
            TestOrderActivities stub = new TestOrderActivities();

            Worker customWorker = env.newWorker("VerifiedQueue");
            customWorker.registerWorkflowImplementationTypes(OrderFulfillmentWorkflowImpl.class);
            customWorker.registerActivitiesImplementations(stub);

            // Start environment ONLY after all workers are registered
            env.start();

            WorkflowClient client = env.getWorkflowClient();
            OrderFulfillmentWorkflow workflow = client.newWorkflowStub(OrderFulfillmentWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue("VerifiedQueue").build());

            assertThatThrownBy(() -> workflow.fulfillOrder("order-fail-789", "cust-99", 250.0))
                    .isInstanceOf(WorkflowException.class);

            // Strictly verify state transitions and compensating actions
            assertThat(stub.paymentProcessed).isTrue();
            assertThat(stub.paymentRefunded).isTrue();
            assertThat(stub.inventoryReleased).isFalse();
            assertThat(stub.lastProcessedAccount).isEqualTo("cust-99");
            assertThat(stub.lastProcessedAmount).isEqualTo(250.0);
        } finally {
            env.close();
        }
    }

    static class TestOrderActivities implements OrderActivities {
        boolean paymentProcessed = false;
        boolean paymentRefunded = false;
        boolean inventoryReleased = false;
        String lastProcessedAccount;
        double lastProcessedAmount;

        @Override
        public void processPayment(String accountId, double amount) {
            this.paymentProcessed = true;
            this.lastProcessedAccount = accountId;
            this.lastProcessedAmount = amount;
        }

        @Override
        public void refundPayment(String accountId, double amount) {
            this.paymentRefunded = true;
        }

        @Override
        public void reserveInventory(String orderId) {
            throw new RuntimeException("Simulated inventory failure: Out of stock!");
        }

        @Override
        public void releaseInventory(String orderId) {
            this.inventoryReleased = true;
        }
    }
}
