# Domain-Driven Design (DDD) Beginner Guide Companion Workshop

Companion repository for the article: **[Domain-Driven Design Explained Simply: A Beginner's Complete Guide](https://learn.webcodein.com/post/domain-driven-design-explained-beginners-guide)**.

This workshop provides a complete, working implementation of Domain-Driven Design (DDD) concepts using **Java 25**, **Spring Boot 4.0.0**, **Hibernate 7.1**, and **ArchUnit 1.4.1** in an online bookstore order management domain.

---

## Architectural Overview

This project implements a clean layered architecture demonstrating strategic and tactical DDD patterns:

```
src/main/java/com/webcodein/workshop/architecture/ddd/
├── domain/                               # Core Domain Layer (Pure Java, Framework-Independent)
│   ├── model/order/
│   │   ├── Order.java                    # Aggregate Root encapsulating lines and business invariants
│   │   ├── OrderLine.java                # Entity inside Aggregate boundary
│   │   ├── OrderId.java                  # Value Object (Java 25 Record)
│   │   ├── BookId.java                   # Value Object (Java 25 Record)
│   │   ├── Money.java                    # Value Object with safe currency operations (Record)
│   │   ├── Quantity.java                 # Value Object with bounds validation (Record)
│   │   ├── CustomerEmail.java            # Value Object with regex validation (Record)
│   │   └── OrderStatus.java              # Domain Enum (DRAFT, CONFIRMED, CANCELLED)
│   ├── event/
│   │   ├── OrderPlacedEvent.java         # Domain Event published upon order creation
│   │   └── OrderConfirmedEvent.java      # Domain Event published upon confirmation
│   ├── repository/
│   │   └── OrderRepository.java          # Pure domain repository interface
│   └── service/
│       └── PricingService.java           # Domain service calculating VIP volume discounts
├── application/                          # Application Layer (Use Cases & Orchestration)
│   ├── OrderApplicationService.java      # Transactional boundary orchestrating domain model
│   ├── CreateOrderCommand.java           # Ingress command DTO
│   ├── AddItemCommand.java               # Ingress command DTO
│   ├── OrderDto.java                     # Egress presentation DTO
│   └── OrderLineDto.java                 # Egress line item DTO
├── infrastructure/                       # Infrastructure Layer (Adapters & Persistence)
│   └── persistence/
│       ├── OrderJpaEntity.java           # Relational JPA entity
│       ├── OrderLineJpaEntity.java       # Relational JPA line entity
│       ├── SpringDataOrderRepository.java# Spring Data JPA interface
│       └── JpaOrderRepositoryAdapter.java# Implements domain OrderRepository interface
└── presentation/                         # Presentation Layer (REST API)
    └── OrderController.java              # Spring Web MVC REST endpoints
```

---

## Tactical DDD Rules Enforced in Code

1. **All Modifications Go Through Aggregate Root**: External code cannot mutate `OrderLine` directly. All operations (`addLine`, `confirm`, `cancel`) pass through `Order`.
2. **Defensive Immutability & Copying**: `order.getLines()` returns an unmodifiable list. Value Objects (`Money`, `OrderId`, `CustomerEmail`, `Quantity`) are immutable Java 25 records.
3. **Business Invariant Protection**:
   - Items can only be added to `DRAFT` orders.
   - An order cannot exceed 50 distinct line items.
   - Confirmation is rejected if the order contains zero items.
   - Cancelling an already cancelled order throws an `IllegalStateException`.
4. **Compile-Time Layer Governance with ArchUnit**:
   - `DddArchitectureTest` enforces that the `domain` package never depends on outer layers (`application`, `presentation`, `infrastructure`) or third-party web/ORM frameworks.

---

## Prerequisites

- **Java 25** (or compatible JDK 25+)
- **Maven 3.9+** (or use included `./mvnw`)
- **Docker Desktop** (optional; default profile runs on in-memory H2)

---

## Build and Test Suite

Execute the comprehensive 38-test suite (covering domain invariants, value object math, application services, MockMvc web slice tests, end-to-end JPA persistence, and ArchUnit architecture checks):

```bash
mvn -B -ntp clean test
```

Execute full verification and packaging:

```bash
mvn -B -ntp clean verify
```

### Test Breakdown (38 Tests, 100% Pass)
- `MoneyTest`: 7 unit tests covering scale rounding, negative bounds, currency mismatch protection, and multiplication.
- `CustomerEmailTest`: 3 unit tests covering email validation and normalization.
- `QuantityTest`: 4 unit tests covering 1-100 bounds and addition.
- `OrderTest`: 8 unit tests validating Aggregate Root lifecycle, state guards, and domain event dispatch.
- `PricingServiceTest`: 3 unit tests verifying VIP discount calculation.
- `OrderApplicationServiceTest`: 3 unit tests verifying command orchestration and event publishing.
- `OrderControllerTest`: 6 `@WebMvcTest` slice tests with `MockMvc` verifying HTTP contracts and status codes (201, 200, 400, 404, 409).
- `BookstoreApplicationTests`: 1 Spring Boot context & JPA integration test executing full order lifecycle end-to-end.
- `DddArchitectureTest`: 3 ArchUnit rules verifying layer boundary isolation and interface contracts.

---

## Running the Application Locally

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

---

## Sample REST API Verification

### 1. Create a Draft Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail": "reader@example.com"}'
```
Response (`201 Created`):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "customerEmail": "reader@example.com",
  "status": "DRAFT",
  "totalAmount": 0.00,
  "currency": "USD",
  "lines": []
}
```

### 2. Add an Item to Order
```bash
curl -X POST http://localhost:8080/api/orders/3fa85f64-5717-4562-b3fc-2c963f66afa6/items \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": "BOOK-DDD-2026",
    "quantity": 2,
    "unitPrice": 49.99,
    "currency": "USD"
  }'
```
Response (`200 OK`):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "customerEmail": "reader@example.com",
  "status": "DRAFT",
  "totalAmount": 99.98,
  "currency": "USD",
  "lines": [
    {
      "bookId": "BOOK-DDD-2026",
      "quantity": 2,
      "unitPrice": 49.99,
      "subtotal": 99.98,
      "currency": "USD"
    }
  ]
}
```

### 3. Confirm the Order
```bash
curl -X POST http://localhost:8080/api/orders/3fa85f64-5717-4562-b3fc-2c963f66afa6/confirm
```
Response (`200 OK`):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "customerEmail": "reader@example.com",
  "status": "CONFIRMED",
  "totalAmount": 99.98,
  "currency": "USD"
}
```

---

## Related Guides & Tutorials

- [Bounded Context in DDD Explained](https://learn.webcodein.com/post/bounded-context-ddd-explained-beginners)
- [DDD Layers Explained: Where Does Each Piece of Code Go?](https://learn.webcodein.com/post/ddd-layers-explained-code-structure-spring-boot)
- [Building Modular Monoliths with Spring Modulith and Spring Boot 4](https://learn.webcodein.com/post/spring-modulith-modular-monoliths-spring-boot-4)
- [Enforcing DDD Tactical Patterns with ArchUnit in Spring Boot 4](https://learn.webcodein.com/post/archunit-enforce-ddd-tactical-patterns-spring-boot)
