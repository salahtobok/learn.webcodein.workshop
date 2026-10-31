package com.webcodein.workshop.saga;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final WorkflowClient workflowClient;

    public OrderController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitOrder(@RequestBody CreateOrderRequest request) {
        if (request == null || request.orderId() == null || request.customerId() == null || request.amount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Invalid order payload: orderId, customerId, and positive amount are required."
            ));
        }

        String workflowId = "order-saga-" + request.orderId();
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(SagaApplication.TASK_QUEUE)
                .build();

        OrderFulfillmentWorkflow workflow = workflowClient.newWorkflowStub(OrderFulfillmentWorkflow.class, options);

        try {
            workflow.fulfillOrder(request.orderId(), request.customerId(), request.amount());
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "orderId", request.orderId(),
                    "workflowId", workflowId,
                    "message", "Order fulfilled successfully through Temporal Saga orchestration."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "COMPENSATED",
                    "orderId", request.orderId(),
                    "workflowId", workflowId,
                    "error", e.getMessage() != null ? e.getMessage() : "Saga failure: compensations executed."
            ));
        }
    }

    public record CreateOrderRequest(String orderId, String customerId, double amount) {}
}
