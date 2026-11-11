# DDD vs CRUD: Visual Guide for Java Developers

Companion workshop code for the tutorial: [DDD vs CRUD: Visual Guide for Java Developers | When to Use Which](https://learn.webcodein.com/post/ddd-vs-crud-visual-guide-java-developers).

## Overview

This project demonstrates the exact same e-commerce Order Management domain implemented side-by-side using two contrasting software architectures in modern Java 25 and Spring Boot 4:

1. **Traditional Anemic CRUD (`com.webcodein.workshop.crud`)**:
   - `OrderEntity`: Dumb data container with open public getters and setters (Anemic Domain Model).
   - `OrderService`: Service layer containing all validation rules, state mutation, and business logic.
   - `OrderRepository`: Standard Spring Data `JpaRepository`.
   - `CrudOrderController`: REST endpoints exposing entity state (`/api/crud/orders`).
2. **Domain-Driven Design (`com.webcodein.workshop.ddd`)**:
   - **Domain Layer (`domain`)**:
     - `Order`: Rich Aggregate Root encapsulating internal state, guarding invariants, and exposing explicit business behavior (`fulfill()`, `cancel(reason)`).
     - `Email`: Immutable Value Object (Java 25 Record) guaranteeing valid email syntax upon instantiation.
     - `Money`: Immutable Value Object (Java 25 Record) with currency bounds checking and arithmetic logic.
     - `OrderStatus`: Strict domain enum (`CREATED`, `PAID`, `FULFILLED`, `CANCELLED`).
     - `OrderRepository`: Framework-agnostic domain port interface.
   - **Infrastructure Layer (`infrastructure`)**:
     - `OrderJpaEntity`: Dedicated database table mapping (`ddd_orders`) with `@Version` optimistic locking.
     - `SpringDataOrderRepository`: Spring Data JPA implementation.
     - `OrderRepositoryImpl`: Domain repository adapter cleanly mapping between JPA entities and pure domain aggregates via `Order.reconstitute(...)`.
   - **Application Layer (`application`)**:
     - `OrderApplicationService`: Orchestrates use cases, manages transaction boundaries, and converts entities to immutable `OrderDto` records.
   - **Presentation Layer (`presentation`)**:
     - `DddOrderController`: Clean REST API (`/api/ddd/orders`).

## Prerequisites

- Java 25 JDK
- Maven 3.9+
- Docker (optional, for running with PostgreSQL via `docker-compose`)

## Building and Testing

Run the complete test suite (unit tests, WebMvc slices, ArchUnit rules, and full Spring Boot integration):

```bash
mvn clean test
```

Build the executable Spring Boot artifact:

```bash
mvn clean verify
```

### Architectural Enforcement with ArchUnit

The project contains automated architectural rules in `DddArchitectureTest`:
- **Domain Purity**: Verifies that `com.webcodein.workshop.ddd.domain..` depends only on itself and standard Java (`java..`), with zero dependencies on Spring, JPA, or web libraries.
- **Package Isolation**: Proves that the CRUD and DDD packages do not cross-import or leak abstractions into each other.
- **Service & Controller Annotations**: Validates that all application services use `@Service` and controllers use `@RestController`.

## API Endpoints

### Traditional CRUD Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/crud/orders` | Create order entity via service |
| `GET` | `/api/crud/orders/{id}` | Read raw order entity |
| `GET` | `/api/crud/orders` | List all order entities |
| `PUT` | `/api/crud/orders/{id}/fulfill` | Fulfill order via service logic |
| `PUT` | `/api/crud/orders/{id}/cancel` | Cancel order via service logic |

### Domain-Driven Design Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/ddd/orders` | Create order aggregate via command |
| `GET` | `/api/ddd/orders/{id}` | Read order DTO |
| `GET` | `/api/ddd/orders` | List all order DTOs |
| `PUT` | `/api/ddd/orders/{id}/fulfill` | Fulfill order via Aggregate business method |
| `PUT` | `/api/ddd/orders/{id}/cancel` | Cancel order via Aggregate business method |
