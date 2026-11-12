# Enterprise Event-Driven CQRS Architecture with Apache Kafka and Spring Boot 4

Companion workshop repository for the technical article:  
**[Enterprise Event-Driven CQRS Architecture with Apache Kafka and Spring Boot 4](https://learn.webcodein.com/post/cqrs-event-driven-architecture-kafka-spring-boot-4)**

---

## Architecture Overview

This project implements the Command Query Responsibility Segregation (CQRS) architectural pattern using Spring Boot 4, Java 25, Apache Kafka, and PostgreSQL. It demonstrates how to decouple high-throughput write operations (Command model) from fast, read-optimized projections (Query model) via asynchronous, partitioned Kafka event streams.

```
cqrs-event-driven-architecture-kafka-spring-boot-4
├── src
│   ├── main/java/com/webcodein/cqrs
│   │   ├── OrderCqrsApplication.java          # Spring Boot main entrypoint
│   │   ├── command
│   │   │   ├── Order.java                     # Write-side JPA entity
│   │   │   ├── OrderCommandController.java    # POST /api/orders
│   │   │   ├── OrderCommandService.java       # Transactional write & Kafka publisher
│   │   │   ├── OrderRepository.java           # Relational write repository
│   │   │   └── OrderRequest.java              # Command payload DTO
│   │   ├── event
│   │   │   └── OrderCreatedEvent.java         # Immutable domain event record
│   │   └── query
│   │       ├── OrderQueryController.java      # GET /api/orders/summary/{customerId}
│   │       ├── OrderQueryService.java         # @KafkaListener read model projector
│   │       ├── OrderSummaryRepository.java    # Read-optimized projection repository
│   │       └── OrderSummaryView.java          # Denormalized projection entity
│   └── test/java/com/webcodein/cqrs
│       ├── CqrsIntegrationTest.java           # Kafka + PostgreSQL Testcontainers
│       ├── architecture
│       │   └── CqrsArchitectureTest.java      # 5 ArchUnit CQRS boundary rules
│       ├── command
│       │   ├── OrderCommandControllerTest.java# MockMvc slice testing
│       │   └── OrderTest.java                 # Domain entity unit tests
│       ├── event
│       │   └── OrderCreatedEventTest.java     # Event record unit tests
│       └── query
│           ├── OrderQueryControllerTest.java  # MockMvc query slice testing
│           └── OrderSummaryViewTest.java      # Projection view unit tests
├── docker-compose.yml
└── pom.xml
```

---

## Architectural Boundaries Enforced

The test suite in [`CqrsArchitectureTest.java`](src/test/java/com/webcodein/cqrs/architecture/CqrsArchitectureTest.java) executes 5 automated architectural fitness checks:

1. **Command-to-Query Isolation**: Classes in `..command..` must never depend on classes in `..query..`.
2. **Query-to-Command Isolation**: Classes in `..query..` must never depend on classes in `..command..`.
3. **Event Immutability**: All classes in `..event..` must be declared as immutable Java records.
4. **Controller Isolation**: REST controllers must not cross-depend on each other.
5. **Repository Encapsulation**: Data access repositories must only be accessed within their respective command or query boundaries.

---

## Build and Test

### Prerequisites
- **JDK 25** (or JDK 21+)
- **Maven 3.9+**
- **Docker running locally** (for PostgreSQL 16 and Confluent Kafka 7.4 Testcontainers)

### Execution
Run the full test suite (14 unit, slice, ArchUnit, and Testcontainers integration tests):

```bash
mvn clean test
```

Expected output:
```text
[INFO] Running com.webcodein.cqrs.command.OrderTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.cqrs.query.OrderSummaryViewTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.cqrs.event.OrderCreatedEventTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.cqrs.command.OrderCommandControllerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.cqrs.query.OrderQueryControllerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.cqrs.architecture.CqrsArchitectureTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.cqrs.CqrsIntegrationTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## Running Locally with Docker Compose

Start PostgreSQL and Redpanda (lightweight Kafka alternative):

```bash
docker-compose up -d
mvn spring-boot:run
```

### Sample HTTP Requests

**1. Place an Order (Command Side):**
```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "USER-999", "totalAmount": 499.99}'
```

Response:
```json
{
  "id": 1,
  "customerId": "USER-999",
  "totalAmount": 499.99,
  "status": "CREATED"
}
```

**2. Query Projected Order Summary (Query Side):**
```bash
curl -i http://localhost:8080/api/orders/summary/USER-999
```

Response:
```json
[
  {
    "orderId": 1,
    "customerId": "USER-999",
    "totalAmount": 499.99,
    "displayStatus": "Pending Fulfillment"
  }
]
```

---

## Continuous Integration

This project is registered in the GitHub Actions build matrix:  
[`.github/workflows/build.yml`](../../.github/workflows/build.yml)
