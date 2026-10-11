# Implementing the Saga Pattern with Spring Boot 4 and Temporal

This is the companion code for the tutorial on implementing distributed transaction orchestration using the **Saga Pattern**, **Spring Boot 4**, and the **Temporal Java SDK**.

## Requirements
- Java 25
- Maven 3.9+ (or use `./mvnw`)

## Running the Tests
The automated unit and integration tests use Temporal's in-memory `TestWorkflowEnvironment` to verify both the happy path and the compensation path without requiring an external Temporal cluster.

```bash
./mvnw clean verify
```

On Windows:
```cmd
mvnw.cmd clean verify
```

## Features Demonstrated
1. **Activity Interfaces**: `@ActivityInterface` definitions for payment processing, refunds, inventory reservation, and inventory release.
2. **Workflow Orchestration**: `@WorkflowInterface` and `OrderFulfillmentWorkflowImpl` leveraging Temporal's built-in `io.temporal.workflow.Saga` for automatic, reverse-order compensations upon failure.
3. **Temporal Worker**: Bootstrapping worker instances via Spring Boot `CommandLineRunner` listening on `OrderFulfillmentTaskQueue`.
