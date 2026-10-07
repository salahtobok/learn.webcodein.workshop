package com.webcodein.workshop.saga;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testSuccessfulSaga() {
        testEnv.start();
        
        OrderFulfillmentWorkflow workflow = workflowClient.newWorkflowStub(OrderFulfillmentWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(SagaApplication.TASK_QUEUE).build());

        // Should complete without exception
        workflow.fulfillOrder("order-123", "cust-1", 100.0);
    }

    @Test
    void testFailedSagaExecutesCompensations() {
        testEnv.start();

        OrderFulfillmentWorkflow workflow = workflowClient.newWorkflowStub(OrderFulfillmentWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(SagaApplication.TASK_QUEUE).build());

        // orderId containing "fail" will throw exception in reserveInventory
        assertThatThrownBy(() -> workflow.fulfillOrder("order-fail-456", "cust-1", 100.0))
                .isInstanceOf(WorkflowException.class);
    }
}
