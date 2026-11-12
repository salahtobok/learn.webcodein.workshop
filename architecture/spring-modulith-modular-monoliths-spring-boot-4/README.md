# Spring Modulith: Modular Monoliths in Spring Boot 4

Companion code repository for the complete architecture guide on [learn.webcodein.com](https://learn.webcodein.com/post/spring-modulith-modular-monoliths-spring-boot-4).

This enterprise reference project demonstrates how to structure, encapsulate, and verify a **Modular Monolith** in Java 25 using **Spring Boot 4.0.0**, **Spring Modulith**, and **ArchUnit 1.4.1**.

---

## Architectural Highlights

- **Domain-Driven Module Boundaries**: Each direct subpackage under `com.webcodein.ecommerce` represents an isolated, self-contained business module (`order`, `inventory`).
- **Encapsulated Module APIs**: Public classes and Java records define explicit module APIs, while internal implementation details and data access repositories remain package-private.
- **Asynchronous Inter-Module Communication**: The `order` module publishes `OrderPlacedEvent` without directly injecting or coupling with the `inventory` module.
- **Transactional Event Processing**: The `inventory` module reacts asynchronously using `@ApplicationModuleListener`, ensuring decoupled execution in an isolated transaction.
- **Architectural Verification with ArchUnit & Modulith**: `ModularityTests` uses `ApplicationModules.of(...).verify()` to statically detect illegal cross-module dependencies and circular references at test time, while `ModulithArchitectureTest` enforces structural conventions.
- **Automated Living Documentation**: Automatic generation of PlantUML component models and module canvases during build execution.
- **Full Test Suite (33 Tests)**: Domain invariant tests, WebMvc slice tests, service mocks, integration scenarios, and architectural rules.

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
    │   │   │   ├── InventoryItem.java                    # JPA Entity (data model & domain validation)
    │   │   │   ├── InventoryRepository.java              # JPA Repository (data access interface)
    │   │   │   └── InventoryService.java                 # @ApplicationModuleListener for OrderPlacedEvent & stock management
    │   │   └── order/                                    # ORDER MODULE
    │   │       ├── Order.java                            # JPA Entity (data model & state machine)
    │   │       ├── OrderController.java                  # REST controller (POST /api/orders, GET /api/orders/{id})
    │   │       ├── OrderPlacedEvent.java                 # Public Domain Event Record (Module API)
    │   │       ├── OrderRequest.java                     # Immutable input DTO record with compact validation
    │   │       ├── OrderResponse.java                    # Immutable output DTO record projection
    │   │       ├── OrderRepository.java                  # JPA Repository (data access interface)
    │   │       └── OrderService.java                     # Transactional order placement & event publisher
    │   └── resources/
    │       └── application.properties                    # H2 in-memory database and logging configuration
    └── test/
        └── java/com/webcodein/ecommerce/
            ├── ModularityTests.java                      # Spring Modulith boundary verification & PlantUML docs (2 tests)
            ├── ModulithArchitectureTest.java             # ArchUnit 1.4.1 structural & package isolation rules (5 tests)
            ├── inventory/
            │   ├── InventoryItemTest.java                # Entity invariant and stock arithmetic tests (5 tests)
            │   └── InventoryServiceTest.java             # Mockito unit tests for event handling & restocking (4 tests)
            └── order/
                ├── OrderTest.java                        # Entity invariants and state transitions (5 tests)
                ├── OrderRequestTest.java                 # DTO validation tests (3 tests)
                ├── OrderPlacedEventTest.java             # Event record immutability tests (1 test)
                ├── OrderServiceTest.java                 # Unit tests for order placement & event capture (3 tests)
                ├── OrderControllerTest.java              # MockMvc slice tests for REST endpoints (4 tests)
                └── OrderIntegrationTest.java             # End-to-end Spring context integration test (1 test)
```

---

## Build and Test Suite

Execute the full 33-test suite:
```bash
mvn clean test
```

---

## REST API Reference

### 1. Place Order (Publishes Transactional Event)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "LAPTOP-X",
    "quantity": 2
  }'
```

### 2. Retrieve Order by ID
```bash
curl -X GET http://localhost:8080/api/orders/1
```
