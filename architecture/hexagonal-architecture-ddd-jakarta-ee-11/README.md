# Hexagonal Architecture & DDD in Jakarta EE 11 with Spring Boot 4

This is the official companion code for the in-depth architectural guide:
[The Definitive Guide to Hexagonal Architecture & DDD in Jakarta EE 11](https://learn.webcodein.com/post/hexagonal-architecture-ddd-jakarta-ee-11)

---

## 1. Architectural Overview

This project showcases a clean **Hexagonal Architecture (Ports and Adapters)** integrated with tactical **Domain-Driven Design (DDD)** on **Java 25**, **Spring Boot 4.0.0**, and **Jakarta EE 11**:

- **Domain Core (The Hexagon)**:
  - `Order` Aggregate Root encapsulating business invariants and state transitions (`CREATED -> PAID`).
  - `Money` & `OrderId` immutable Java 25 Value Objects enforcing validation guards (zero/negative amount protection).
  - Zero external framework dependencies (`jakarta.persistence.*`, `jakarta.ws.rs.*`, or Spring annotations are strictly forbidden in `domain.model`).
- **Driving (Inbound) Port & Adapter**:
  - Inbound Port: `CreateOrderUseCase` interface defining application use case orchestration.
  - Inbound Driving Adapter: `OrderResource` exposing a Jakarta RESTful Web Services 3.1 (`jakarta.ws.rs`) endpoint under `@Path("/api/orders")`.
- **Driven (Outbound) Port & Adapter**:
  - Outbound Port: `OrderPort` interface defining persistence contracts (`save`, `findById`).
  - Outbound Driven Adapter: `OrderPersistenceAdapter` mapping pure domain entities to `OrderEntity` JPA structures.
  - Modern Persistence: `OrderRepository` using the **Jakarta Data 1.0** specification (`jakarta.data.repository.CrudRepository`) powered by Hibernate 7.1 ORM and PostgreSQL 16.
- **Architectural Governance**:
  - `HexagonalArchitectureTest` powered by **ArchUnit 1.4.1** enforces compile-time architectural integrity and strict package isolation.

---

## 2. Package Structure

```text
com.webcodein.hexagonal
├── HexagonalApplication.java                  # Spring Boot 4 application bootstrapper
├── JerseyConfig.java                          # JAX-RS ResourceConfig registration
├── domain
│   ├── model
│   │   ├── Money.java                         # Value Object: Immutable monetary value & invariant guards
│   │   ├── Order.java                         # Aggregate Root: State transitions & business invariants
│   │   └── OrderId.java                       # Value Object: Strongly-typed identity
│   ├── port
│   │   ├── in
│   │   │   └── CreateOrderUseCase.java        # Driving Inbound Port interface
│   │   └── out
│   │       └── OrderPort.java                 # Driven Outbound Port interface
│   └── service
│       └── OrderService.java                  # Application Service orchestrating use cases
└── infrastructure
    └── adapter
        ├── in
        │   └── rest
        │       └── OrderResource.java         # Driving Adapter: JAX-RS 3.1 REST resource
        └── out
            └── persistence
                ├── OrderEntity.java           # JPA entity mapping to 'orders' table
                ├── OrderPersistenceAdapter.java # Driven Adapter implementing OrderPort
                └── OrderRepository.java       # Jakarta Data 1.0 CrudRepository
```

---

## 3. Tech Stack

- **Language:** Java 25 (`release 25`)
- **Framework:** Spring Boot 4.0.0 (Spring Framework 7)
- **Jakarta EE APIs:** Jakarta EE 11 (Jakarta RESTful Web Services 3.1 / Jersey, Jakarta Data 1.0.1, Jakarta Inject / CDI)
- **Persistence & ORM:** Hibernate 7.1.0 (`hibernate-core`, `hibernate-processor`)
- **Database:** PostgreSQL 16
- **Testing & Governance:** JUnit 5, Mockito 5, Testcontainers 2.0.2 (`testcontainers-postgresql`), ArchUnit 1.4.1

---

## 4. Running the Application

### Prerequisites
- JDK 25 installed and configured (`JAVA_HOME`)
- Docker Desktop running (for PostgreSQL 16 containerization)

### Local Maven Execution
```bash
./mvnw spring-boot:run
```

The application starts on port `8080`.

### Containerized Execution with Docker
```bash
docker build -t hexagonal-ddd-jakarta-ee .
docker run -p 8080:8080 hexagonal-ddd-jakarta-ee
```

---

## 5. Verification & Testing

### Executing the Test Suite
Run all unit, integration, and ArchUnit architectural governance fitness tests:

```bash
./mvnw clean verify
```

### Test Suite Inventory (14 Tests, 100% Passing)
1. **`MoneyTest` (4 tests)**: Verifies valid money instances, zero amounts, and invariant exception throwing on negative or null amounts.
2. **`OrderTest` (3 tests)**: Verifies aggregate initial state, valid state machine transition (`CREATED -> PAID`), and invalid transition rejections.
3. **`OrderServiceTest` (1 test)**: Verifies mock port delegation and domain orchestrations.
4. **`OrderResourceTest` (1 test)**: Verifies JAX-RS 201 Created HTTP response mapping, DTO payloads, and use case invocations.
5. **`HexagonalArchitectureTest` (4 ArchUnit fitness tests)**:
   - `domain_should_not_depend_on_infrastructure`: Protects domain from external framework bleeding.
   - `domain_model_should_not_depend_on_jpa_or_jaxrs`: Ensures domain classes have zero JPA/JAX-RS/Spring imports.
   - `inbound_adapters_should_not_depend_on_outbound_adapters`: Enforces strict isolation between ports.
   - `ports_should_be_interfaces`: Guarantees port decoupling via interfaces.
6. **`HexagonalApplicationTests` (1 test)**: Spring Boot context load with live PostgreSQL 16 Testcontainers.

### Sample API Requests (Curl)

#### Create an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-9874",
    "amount": 149.99
  }'
```

Expected Response (`201 Created`):
```json
{
  "id": "e4b2d184-7a32-47c3-832f-4899b8f2d512",
  "status": "CREATED"
}
```
