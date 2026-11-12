# DDD Building Blocks in Java 25: Entity, Value Object, and Aggregate Root

[![Build Status](https://github.com/salahtobok/learn.webcodein.workshop/actions/workflows/build.yml/badge.svg)](https://github.com/salahtobok/learn.webcodein.workshop/actions/workflows/build.yml)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-2.0.2-purple.svg)](https://testcontainers.com/)
[![ArchUnit](https://img.shields.io/badge/ArchUnit-1.4.1-red.svg)](https://www.archunit.org/)

Companion workshop code for the comprehensive guide:  
[Build a DDD Core with Java 25 and Spring Boot 4: Step-by-Step Tutorial](https://learn.webcodein.com/post/ddd-building-blocks-java-25-entity-value-object-aggregate)

---

## Architectural Overview

Domain-Driven Design (DDD) tactical patterns protect enterprise business logic from framework contamination and database concerns:

1. **Entity**: An object defined by its persistent identity (`id`), rather than its attributes. `OrderItem` is an entity whose identity remains stable throughout state changes.
2. **Value Object**: An immutable object defined entirely by its structural attributes without identity. We use modern Java 25 `record` features (`Money`) annotated with JPA `@Embeddable`.
3. **Aggregate Root**: The cluster of associated entities and value objects treated as a single unit of consistency. `Order` is the aggregate root. External callers cannot modify child `OrderItem` instances directly; all mutations must pass through methods on `Order` (`addItem`, `calculateTotal`).
4. **Repository**: Encapsulates persistence logic and provides aggregate-oriented data access (`OrderRepository`). Only aggregate roots have repositories.

---

## Technical Stack

| Component | Technology | Version |
|---|---|---|
| Language | OpenJDK / GraalVM | Java 25 |
| Framework | Spring Boot | 4.0.0 GA |
| Persistence | Spring Data JPA / Hibernate | 7.1 |
| Database | PostgreSQL | 16-alpine |
| Integration Testing | Testcontainers | 2.0.2 |
| Architectural Fitness | ArchUnit | 1.4.1 |

---

## Project Structure

```
ddd-building-blocks-java-25-entity-value-object-aggregate/
├── pom.xml
├── compose.yaml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/webcodein/ecommerce/
    │   │   ├── Application.java               # Spring Boot entry point
    │   │   ├── domain/
    │   │   │   ├── Money.java                 # Immutable Value Object (Java 25 Record + @Embeddable)
    │   │   │   ├── Order.java                 # Aggregate Root Entity (@Table orders)
    │   │   │   └── OrderItem.java             # Child Entity within aggregate boundary (@Table order_items)
    │   │   ├── repository/
    │   │   │   └── OrderRepository.java       # Aggregate repository interface
    │   │   └── web/
    │   │       └── OrderController.java       # Presentation REST controller (/api/orders)
    │   └── resources/
    │       └── application.properties         # Database and JPA configuration
    └── test/
        └── java/com/webcodein/ecommerce/
            ├── domain/
            │   ├── MoneyTest.java             # Value object invariant & behavior unit tests (8 tests)
            │   ├── OrderTest.java             # Aggregate root consistency & total calculation tests (6 tests)
            │   └── OrderItemTest.java         # Child entity invariant tests (3 tests)
            ├── web/
            │   └── OrderControllerTest.java   # WebMvc slice tests with @MockitoBean (5 tests)
            ├── architecture/
            │   └── DddArchitectureTest.java   # ArchUnit architectural fitness tests (5 rules)
            └── OrderIntegrationTest.java      # PostgreSQL Testcontainers full integration test (1 test)
```

---

## Quickstart & Local Execution

### 1. Build and Run Tests

```bash
mvn clean test
```

Executes 28 automated tests covering:
- Value object immutability and domain algebra (`Money.add`, `Money.multiply`)
- Aggregate boundary invariant enforcement
- WebMvc REST controller status codes and JSON paths
- ArchUnit architectural governance rules
- Real PostgreSQL 16 database transactions via Testcontainers

### 2. Run Locally via Docker Compose

```bash
docker compose up --build
```

### 3. REST API Usage

#### Create an Order
```bash
curl -X POST "http://localhost:8080/api/orders?customerId=CUST-101"
```

#### Add an Item to the Order
```bash
curl -X POST "http://localhost:8080/api/orders/<ORDER_ID>/items?productId=PROD-9&price=149.99&currency=USD&qty=2"
```

#### Get Order by ID
```bash
curl http://localhost:8080/api/orders/<ORDER_ID>
```
