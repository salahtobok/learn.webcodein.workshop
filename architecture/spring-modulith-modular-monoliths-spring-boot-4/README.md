# Spring Modulith: Modular Monoliths in Spring Boot 4

Companion code repository for the complete architecture guide on [learn.webcodein.com](https://learn.webcodein.com/post/spring-modulith-modular-monoliths-spring-boot-4).

This enterprise reference project demonstrates how to structure, encapsulate, and verify a **Modular Monolith** in Java 25 using **Spring Boot 4.0.0** and **Spring Modulith 1.3+ / 2.0**.

---

## Architectural Highlights

- **Domain-Driven Module Boundaries**: Each direct subpackage under `com.webcodein.ecommerce` represents an isolated, self-contained business module (`order`, `inventory`).
- **Encapsulated Module APIs**: Public classes and Java records define explicit module APIs, while internal implementation details and data access repositories remain package-private.
- **Asynchronous Inter-Module Communication**: The `order` module publishes `OrderPlacedEvent` without directly injecting or coupling with the `inventory` module.
- **Transactional Event Processing**: The `inventory` module reacts asynchronously using `@ApplicationModuleListener`, ensuring decoupled execution in an isolated transaction.
- **Architectural Verification with ArchUnit**: `ModularityTests` uses `ApplicationModules.of(...).verify()` to statically detect illegal cross-module dependencies and circular references at test time.
- **Automated Living Documentation**: Automatic generation of PlantUML component models and module canvases during build execution.

---

## Project Structure

```
architecture/spring-modulith-modular-monoliths-spring-boot-4/
├── Dockerfile                                            # Multi-stage container build definition
├── mvnw / mvnw.cmd                                       # Maven 3.9+ wrapper (Unix LF line endings)
├── pom.xml                                               # Java 25, Spring Boot 4.0.0, Spring Modulith 1.3.1, ArchUnit 1.4.1
└── src/
    ├── main/
    │   ├── java/com/webcodein/ecommerce/
    │   │   ├── EcommerceApplication.java                 # Main application entry point
    │   │   ├── inventory/                                # INVENTORY MODULE
    │   │   │   ├── InventoryItem.java                    # JPA Entity (package-private data model)
    │   │   │   ├── InventoryRepository.java              # JPA Repository (package-private data access)
    │   │   │   └── InventoryService.java                 # @ApplicationModuleListener for OrderPlacedEvent
    │   │   └── order/                                    # ORDER MODULE
    │   │       ├── Order.java                            # JPA Entity (package-private data model)
    │   │       ├── OrderController.java                  # REST controller (POST /api/orders)
    │   │       ├── OrderPlacedEvent.java                 # Public Domain Event Record (Module API)
    │   │       ├── OrderRepository.java                  # JPA Repository (package-private data access)
    │   │       └── OrderService.java                     # Transactional order placement & event publisher
    │   └── resources/
    │       └── application.properties                    # H2 in-memory database and logging configuration
    └── test/
        └── java/com/webcodein/ecommerce/
            ├── ModularityTests.java                      # Spring Modulith boundary verification & PlantUML docs (2 tests)
            ├── inventory/
            │   └── InventoryServiceTest.java             # Mockito unit tests for event handling & stock deduction (2 tests)
            └── order/
                ├── OrderControllerTest.java              # MockMvc slice tests for REST endpoints (2 tests)
                └── OrderIntegrationTest.java             # End-to-end Spring context integration test (1 test)
```

---

## Prerequisites

- **Java Development Kit (JDK)**: Version 25 (or 21+)
- **Apache Maven**: Version 3.9+ (or use the included `./mvnw`)
- **Docker**: (Optional, for containerized execution)

---

## Build and Verification

Compile the project and run the complete test suite (7 tests):

```bash
mvn clean verify
```

Expected output:
```text
[INFO] Running com.webcodein.ecommerce.inventory.InventoryServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.ecommerce.ModularityTests
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.ecommerce.order.OrderControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.ecommerce.order.OrderIntegrationTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Running the Application

### Local Development

Launch the Spring Boot application:

```bash
mvn spring-boot:run
```

### Docker Container

Build and run using the multi-stage Dockerfile:

```bash
docker build -t spring-modulith-demo .
docker run -d -p 8080:8080 --name modulith-app spring-modulith-demo
```

---

## Testing the REST Endpoints

### 1. Place a New Order (`POST /api/orders`)

Submit an order payload to trigger transactional order placement and asynchronous inventory stock deduction:

```bash
curl -X POST "http://localhost:8080/api/orders" \
     -H "Content-Type: application/json" \
     -d '{
       "productCode": "LAPTOP-X",
       "quantity": 2
     }'
```

Response:
```json
{
  "id": 1,
  "productCode": "LAPTOP-X",
  "quantity": 2,
  "status": "PENDING"
}
```

Console Output:
```text
INFO com.webcodein.ecommerce.inventory.InventoryService : Received OrderPlacedEvent for order: 1
INFO com.webcodein.ecommerce.inventory.InventoryService : Reduced stock for product: LAPTOP-X, new quantity: 8
```

---

## Architectural Documentation Output

Running `ModularityTests` automatically generates PlantUML architectural models in `target/modulith-docs/`:
- `components.puml`: High-level module interaction diagram.
- `module-order.puml`: Granular dependency view of the order module.
- `module-inventory.puml`: Granular dependency view of the inventory module.

---

## Related Articles

- [Building Modular Monoliths with Spring Modulith and Spring Boot 4](https://learn.webcodein.com/post/spring-modulith-modular-monoliths-spring-boot-4)
- [Clean Architecture: Implementing Hexagonal (Ports & Adapters) in Spring Boot 4](https://learn.webcodein.com/post/clean-architecture-hexagonal-ports-adapters-spring-boot-4)
- [Implementing the Saga Pattern with Spring Boot 4 and Temporal](https://learn.webcodein.com/post/saga-pattern-spring-boot-4-temporal)
- [Kafka Transactional Outbox Pattern in Spring Boot 4](https://learn.webcodein.com/post/kafka-transactional-outbox-spring-boot-4)
- [Enforcing Architecture Rules with ArchUnit in Spring Boot](https://learn.webcodein.com/post/archunit-hexagonal-architecture-spring-boot)
