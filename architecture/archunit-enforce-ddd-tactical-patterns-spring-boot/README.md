# Enforcing DDD Tactical Patterns with ArchUnit in Spring Boot 4

This is the official companion code for the architectural guide:
[Enforcing DDD Tactical Patterns with ArchUnit in Spring Boot 4](https://learn.webcodein.com/post/archunit-enforce-ddd-tactical-patterns-spring-boot)

---

## 1. Overview

This project demonstrates how to turn Domain-Driven Design (DDD) tactical patterns into automated, compile-time architectural unit tests using **ArchUnit 1.4.1**, **Spring Boot 4.0.0**, and **Java 25**:

- **Tactical DDD Annotations**:
  - `@AggregateRoot`: Denotes aggregate root boundary gatekeepers.
  - `@DomainEntity`: Marks child entities living strictly within an aggregate boundary.
  - `@ValueObject`: Marks immutable domain values without identity.
  - `@DomainRepository`: Marks repository interfaces.
- **Architectural Rules Enforced**:
  - **Aggregate Root Boundary Rule**: Repositories must only be created for `@AggregateRoot` entities; direct access to child entities (`OrderLine`) is strictly prohibited.
  - **Value Object Immutability Rule**: All fields in `@ValueObject` classes must be `final` with zero setter methods.
  - **Domain Framework Purity Rule**: The domain layer must not depend on presentation or web layers.
  - **Controller Isolation Rule**: Presentation controllers must interact with Application Services, never directly with Repositories.
  - **Service Placement Rule**: Application services must reside strictly in `application.service`.
  - **Layered Architecture Rule**: Strict unidirectional dependencies: `presentation -> application -> domain <- infrastructure`.

---

## 2. Package Structure

```text
com.webcodein.workshop.architecture.archunitenforceddd
├── Application.java                          # Spring Boot 4 main entrypoint
├── application
│   └── service
│       └── OrderService.java                 # Orchestrates use cases & transaction boundaries
├── domain
│   ├── annotations
│   │   ├── AggregateRoot.java                # Meta-annotation for Aggregate Roots
│   │   ├── DomainEntity.java                 # Meta-annotation for Child Entities
│   │   ├── DomainRepository.java             # Meta-annotation for Repositories
│   │   └── ValueObject.java                  # Meta-annotation for Value Objects
│   ├── model
│   │   ├── Money.java                        # Immutable Value Object
│   │   ├── Order.java                        # Aggregate Root
│   │   └── OrderLine.java                    # Child Entity
│   └── repository
│       └── OrderRepository.java              # Domain Repository interface
├── infrastructure
│   └── persistence
│       └── JpaOrderRepository.java          # JPA implementation of OrderRepository
└── presentation
    └── controller
        └── OrderController.java              # REST API endpoint (/api/orders)
```

---

## 3. Tech Stack

- **Java**: Java 25 (`release 25`)
- **Framework**: Spring Boot 4.0.0
- **Architectural Governance**: ArchUnit 1.4.1 (`archunit-junit5`)
- **Persistence**: Spring Data JPA, Hibernate 7.1, H2 (in-memory test runtime), PostgreSQL 16
- **Testing**: JUnit 5, Mockito 5, Spring WebMvc Test

---

## 4. Verification & Test Suite

Run the full test suite (23 tests, 100% pass):

```bash
./mvnw clean test
```

### Test Inventory (23/23 Passing)
- **`DddTacticalArchitectureTest` (6 tests)**:
  - `repositories_must_only_return_aggregate_roots`
  - `value_objects_must_have_only_final_fields`
  - `domain_must_not_depend_on_presentation`
  - `controllers_must_not_access_repositories_directly`
  - `services_must_reside_in_application_package`
  - `layer_dependencies_are_respected`
- **`OrderTest` (6 tests)**: Verifies aggregate invariants, line additions, payment rules, and cancellation transitions.
- **`MoneyTest` (4 tests)**: Verifies value object immutability, negative amount rejections, and value-based equality.
- **`OrderServiceTest` (3 tests)**: Verifies service orchestration, mock repository saving, and not-found error handling.
- **`OrderControllerTest` (3 tests)**: WebMvc slice tests verifying REST API responses, JSON payloads, and HTTP status codes.
- **`ApplicationTests` (1 test)**: Context bootstrapper test.

---

## 5. Sample API Execution (Curl)

### Create an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-42",
    "productCode": "LAPTOP-01",
    "price": 1299.99,
    "currency": "USD"
  }'
```

Expected Response (`201 Created`):
```json
{
  "id": "7b8e5c1d-6a3f-4e2b-9c1a-8f4d2e1a3b5c",
  "customerId": "CUST-42",
  "status": "CREATED"
}
```

### Pay an Order
```bash
curl -X POST http://localhost:8080/api/orders/7b8e5c1d-6a3f-4e2b-9c1a-8f4d2e1a3b5c/pay
```

Expected Response (`200 OK`):
```json
{
  "id": "7b8e5c1d-6a3f-4e2b-9c1a-8f4d2e1a3b5c",
  "customerId": "CUST-42",
  "status": "PAID"
}
```
