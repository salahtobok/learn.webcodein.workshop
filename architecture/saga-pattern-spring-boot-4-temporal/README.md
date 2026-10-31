# Workshop: Implementing the Saga Pattern with Spring Boot 4 and Temporal

Companion code for the in-depth architectural guide: [Implementing the Saga Pattern with Spring Boot 4 and Temporal](https://learn.webcodein.com/post/saga-pattern-spring-boot-4-temporal).

## Overview

In distributed microservices architectures, maintaining data consistency without distributed two-phase commit (2PC) locks requires the **Saga Pattern**. This workshop demonstrates how to implement a robust, orchestrator-driven distributed transaction with **Spring Boot 4.0.0**, **Java 25**, and **Temporal Java SDK**.

## Project Structure

```text
workshop/architecture/saga-pattern-spring-boot-4-temporal/
├── pom.xml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/webcodein/workshop/saga/
│   │   │   ├── SagaApplication.java             # Spring Boot application & Temporal Worker bootstrap
│   │   │   ├── OrderController.java             # REST API endpoint (POST /api/orders)
│   │   │   ├── OrderActivities.java             # @ActivityInterface contract (Payment, Inventory)
│   │   │   ├── OrderActivitiesImpl.java         # Activity implementation with compensation logic
│   │   │   ├── OrderFulfillmentWorkflow.java    # @WorkflowInterface definition
│   │   │   └── OrderFulfillmentWorkflowImpl.java# Orchestrator with Temporal Saga compensation
│   │   └── resources/
│   │       └── application.properties           # Spring Boot & Temporal configuration
│   └── test/
│       └── java/com/webcodein/workshop/saga/
│           ├── OrderActivitiesTest.java         # Activity unit tests (5 tests)
│           ├── OrderControllerTest.java         # WebMvc MockMvc API tests (3 tests)
│           └── OrderFulfillmentWorkflowTest.java# Temporal TestWorkflowEnvironment tests (3 tests)
└── README.md
```

## Key Architectural Concepts

1. **Orchestration vs. Choreography**: Centralized stateful orchestration avoids the "event spaghetti" and circular dependencies common in purely event-driven choreography.
2. **Durable Execution & Event History**: The Temporal Cluster maintains an append-only event log. If a worker pod crashes mid-transaction, a replacement worker seamlessly resumes execution using deterministic replay without duplicate payment processing.
3. **Automatic Reverse-Order Compensation**: Temporal's `io.temporal.workflow.Saga` automatically tracks successful forward activities and triggers compensating actions in reverse order (`refundPayment`, `releaseInventory`) when downstream steps fail.
4. **Zero-Mock In-Memory Testing**: The test suite runs against Temporal's in-memory `TestWorkflowEnvironment`, validating complete workflow execution, activity stubs, and compensation chains without requiring an external cluster or Docker container.

## Prerequisites

- **Java Development Kit (JDK)**: JDK 25
- **Apache Maven**: 3.9+ (or use the included `./mvnw` wrapper)

## Building and Testing

Execute the comprehensive 11-test suite:

```bash
./mvnw clean verify
```

On Windows:
```cmd
mvnw.cmd clean verify
```

### Test Suite Breakdown (11 Tests)

- **`OrderActivitiesTest` (5 tests)**:
  - `testProcessPayment`: Validates payment execution.
  - `testRefundPayment`: Validates refund compensation execution.
  - `testReserveInventorySuccess`: Validates successful inventory reservation.
  - `testReserveInventoryFailure`: Asserts `RuntimeException("Out of stock!")` when order ID contains `fail`.
  - `testReleaseInventory`: Validates inventory release compensation.
- **`OrderControllerTest` (3 tests)**:
  - `shouldReturn200WhenSagaSucceeds`: Verifies HTTP 200 OK and JSON response contract on successful order submission.
  - `shouldReturn400WhenPayloadInvalid`: Verifies HTTP 400 Bad Request on invalid payload attributes.
  - `shouldReturn500WhenSagaFails`: Verifies HTTP 500 Internal Server Error and `status="COMPENSATED"` when workflow throws compensation failure.
- **`OrderFulfillmentWorkflowTest` (3 tests)**:
  - `testSuccessfulSaga`: Executes the complete forward Saga pipeline.
  - `testFailedSagaExecutesCompensations`: Simulates downstream inventory failure and confirms workflow exception wrapping.
  - `testFailedSagaExecutesExactCompensationInReverseOrder`: Uses a test activity stub to strictly verify that `processPayment` succeeded, `refundPayment` was executed, and `releaseInventory` was never triggered.

## Running the Application Locally

1. Start a local Temporal server (e.g. via Temporal CLI):
   ```bash
   temporal server start-dev
   ```
2. Start the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Submit an order via cURL:
   ```bash
   # Happy Path: Successful fulfillment
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"orderId":"order-101","customerId":"cust-88","amount":99.50}'

   # Compensation Path: Out of stock failure triggers compensation
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"orderId":"order-fail-202","customerId":"cust-88","amount":99.50}'
   ```

## Related Resources

- Live Technical Article: [Implementing the Saga Pattern with Spring Boot 4 and Temporal](https://learn.webcodein.com/post/saga-pattern-spring-boot-4-temporal)
- Parent Repository: [learn.webcodein.com](https://github.com/salahtobok/learn.webcodein.com)
