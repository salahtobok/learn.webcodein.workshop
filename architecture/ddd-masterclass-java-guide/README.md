# DDD Masterclass - Java 25 & Spring Boot 4

This is the official companion code for the WebCodein guide: **[Domain-Driven Design (DDD) Masterclass: The Ultimate Java Developer Guide](https://learn.webcodein.com/post/ddd-masterclass-java-guide)**.

It demonstrates production-grade Tactical Domain-Driven Design in **Java 25** with **Spring Boot 4** and **Hibernate 7**:
- **Rich Domain Model**: `Order` Aggregate Root encapsulating business invariants, state machine transitions (`PENDING -> CONFIRMED -> CANCELLED`), and value objects (`OrderItem`).
- **Value Objects**: Immutable Java record types enforcing self-validation and equality semantics.
- **Application Service Layer**: `OrderApplicationService` orchestrating transactional use cases and delegating domain logic to Aggregate Roots.
- **Infrastructure Adapters**: Spring MVC REST controller (`OrderController`) and Spring Data JPA Repository.
- **Architectural Governance**: ArchUnit automated fitness functions enforcing package dependencies and DDD layer isolation at compile time.
- **Integration Testing**: Testcontainers PostgreSQL 16 verifying end-to-end API workflows.

---

## Project Structure

```
src/
├── main/
│   └── java/com/webcodein/ddd/
│       ├── DddApplication.java
│       ├── application/
│       │   └── service/
│       │       └── OrderApplicationService.java
│       ├── domain/
│       │   ├── model/order/
│       │   │   ├── Order.java             # Aggregate Root enforcing invariants
│       │   │   ├── OrderItem.java         # Embeddable Value Object Record
│       │   │   └── OrderStatus.java       # Domain State Enum
│       │   └── repository/
│       │       └── OrderRepository.java   # Domain Repository Interface
│       └── infrastructure/
│           └── web/
│               └── OrderController.java   # Presentation REST Controller
└── test/
    └── java/com/webcodein/ddd/
        ├── DddApplicationTests.java       # Context loads on PostgreSQL Testcontainer
        ├── application/service/
        │   └── OrderApplicationServiceTest.java
        ├── architecture/
        │   └── DddArchitectureTest.java   # ArchUnit architectural rules
        ├── domain/model/order/
        │   ├── OrderItemTest.java         # Value object unit tests
        │   └── OrderTest.java             # Aggregate invariant tests
        └── infrastructure/web/
            └── OrderControllerIntegrationTest.java # Full mockMvc + Testcontainers flow
```

---

## Requirements

- **JDK 25** (Oracle / Amazon Corretto / Eclipse Temurin)
- **Maven 3.9+** (or `./mvnw`)
- **Docker Desktop** (running for Testcontainers integration tests)

---

## Running the Verification Test Suite

To run all unit tests, ArchUnit architectural checks, and Testcontainers integration tests:

```bash
mvn clean verify
```

---

## Running Locally

### Option 1: Docker Compose (Full Stack)
```bash
docker-compose up --build
```
The application starts at `http://localhost:8080`.

### Option 2: Local Spring Boot Run
Start a local PostgreSQL container:
```bash
docker run -d --name ddd-postgres -p 5432:5432 -e POSTGRES_DB=ddddb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:16-alpine
```
Then run the application:
```bash
mvn spring-boot:run
```

---

## API Usage Examples

**1. Create a New Order**
```bash
curl -X POST "http://localhost:8080/api/orders?customerId=123e4567-e89b-12d3-a456-426614174000"
# Response: "53bb0fd3-9737-4571-aefd-ff2e1f255744"
```

**2. Add Items to the Order**
```bash
curl -X POST "http://localhost:8080/api/orders/53bb0fd3-9737-4571-aefd-ff2e1f255744/items?productId=987e6543-e21b-34d5-c678-426614174999&quantity=2&price=99.99"
# Response: 200 OK
```

**3. Confirm the Order**
```bash
curl -X POST "http://localhost:8080/api/orders/53bb0fd3-9737-4571-aefd-ff2e1f255744/confirm"
# Response: 200 OK
```
