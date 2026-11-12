# DDD for CRUD Developers: Progressive Refactoring Workshop

Companion code repository for the enterprise architectural guide:  
**"DDD for CRUD Developers: When and Why to Level Up"**  
Article: [https://learn.webcodein.com/post/ddd-for-crud-developers-when-why-level-up](https://learn.webcodein.com/post/ddd-for-crud-developers-when-why-level-up)

---

## Technical Stack
- **Java 25** (JDK with records, pattern matching, compact constructors)
- **Spring Boot 4.0.0 GA** (Spring MVC, Spring Data JPA, Application Events)
- **ArchUnit 1.4.1** (Automated architectural governance and boundary enforcement)
- **H2 / PostgreSQL** (In-memory testing and production persistence)
- **JUnit 5 & Mockito** (Comprehensive test suite)

---

## Architectural Evolution Overview
This project showcases a progressive 3-stage migration from anemic CRUD to a robust Domain-Driven Design (DDD) model:

1. **Phase 1: Anemic CRUD (`com.webcodein.workshop.ddd.crud`)**
   - Public getters and setters.
   - Business rules and state transitions scattered across bloated `@Service` classes.
   - High risk of illegal states (negative balances, orphan items).

2. **Phase 2: Encapsulated State (`com.webcodein.workshop.ddd.step1`)**
   - Logic pushed down into the entity.
   - Removal of public setters for sensitive business fields.
   - Guard clauses on state transitions.

3. **Phase 3: Rich DDD Aggregate & Hexagonal Ports (`com.webcodein.workshop.ddd.domain`)**
   - **Value Objects:** `Money`, `OrderId`, `CustomerId`, `OrderItem` implemented with Java 25 records and strict validation.
   - **Aggregate Root:** `Order` protecting domain invariants (validating item currencies, preventing excessive discounts, enforcing lifecycle transitions).
   - **Domain Events:** `OrderPlacedEvent` and `OrderShippedEvent` decoupled via Spring's `ApplicationEventPublisher`.
   - **Ports & Adapters:** Application service orchestrating domain operations while domain entities remain pure Java.
   - **REST Presentation:** WebMvc controller returning decoupled DTOs (`OrderResponse`) with HTTP 201/200/400/404 handling.

---

## Project Structure
```text
src/main/java/com/webcodein/workshop/ddd/
├── DddWorkshopApplication.java
├── crud/                         # Legacy Anemic CRUD implementation
│   ├── Order.java
│   ├── OrderRepository.java
│   └── OrderService.java
├── step1/                        # Intermediate encapsulation step
│   └── Order.java
├── domain/                       # Core DDD Domain Layer
│   ├── event/                    # Domain Events
│   │   ├── OrderPlacedEvent.java
│   │   └── OrderShippedEvent.java
│   ├── model/                    # Aggregate Root & Value Objects
│   │   ├── CustomerId.java
│   │   ├── Money.java
│   │   ├── Order.java
│   │   ├── OrderId.java
│   │   ├── OrderItem.java
│   │   └── OrderStatus.java
│   └── repository/               # Outbound Port
│       └── OrderDomainRepository.java
├── application/                  # Application Orchestration Layer
│   └── OrderApplicationService.java
├── infrastructure/               # Inbound/Outbound Adapters
│   └── persistence/
│       └── InMemoryOrderDomainRepository.java
└── presentation/                 # Inbound Web Adapter
    ├── OrderController.java
    └── dto/
        ├── ApplyDiscountRequest.java
        ├── CreateOrderRequest.java
        └── OrderResponse.java
```

---

## Build and Test

Execute the full 31-test suite with Maven and JDK 25:
```bash
mvn -B -ntp clean test
```

### Test Coverage Highlights
- **`MoneyTest` (6 tests):** Validates arithmetic, currency validation, immutability, and zero-decimal rounding.
- **`OrderDomainTest` (10 tests):** Verifies invariant enforcement, line-item subtotal computation, discount rules, and lifecycle transitions.
- **`OrderApplicationServiceTest` (4 tests):** Tests aggregate persistence orchestration and asynchronous domain event publication.
- **`OrderControllerTest` (4 tests):** MockMvc slice testing for HTTP 201 Created, 200 OK, 400 Bad Request, and 404 Not Found.
- **`DddArchitectureTest` (4 tests):** ArchUnit rules verifying that the domain layer never depends on presentation, application, or Spring Web dependencies.
- **`OrderServiceTest` (3 tests):** Legacy CRUD service unit tests.

---

## License
MIT. Free for education and enterprise reference.
