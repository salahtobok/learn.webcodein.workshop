# CQRS and Event Sourcing with Spring Boot 4 and Java 25

Companion code repository for the technical article: [CQRS and Event Sourcing with Spring Boot 4 & Java 25](https://learn.webcodein.com/post/cqrs-event-sourcing-spring-boot-4).

---

## Architecture Overview

This project implements **Command Query Responsibility Segregation (CQRS)** and **Event Sourcing** in Java 25 and Spring Boot 4:
- **Write Side (Command Model):** Receives state-changing requests, validates business invariants, and appends immutable domain events (`OrderCreatedEvent`, `OrderConfirmedEvent`) to the append-only event store (`EventStoreEntity`).
- **Read Side (Query Model / Projection):** Asynchronously listens to domain events via Spring's `ApplicationEventPublisher` and updates a denormalized read model (`OrderReadModel`) optimized for low-latency queries.
- **Java 25 Features:** Utilizes sealed interfaces (`DomainEvent`), records, and pattern matching for switch in `OrderProjector`.

---

## Tech Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.0
- **Database:** PostgreSQL 16 (via Testcontainers and Docker Compose)
- **JSON Processing:** Jackson 2.x with JavaTimeModule
- **Testing:** JUnit 5, AssertJ, Mockito, Spring MockMvc, Testcontainers 2.0.2

---

## Running the Application Locally

### 1. Start PostgreSQL
```bash
docker compose up -d postgres
```

### 2. Run the Spring Boot Application
```bash
./mvnw spring-boot:run
```

### 3. Execute Commands & Queries via REST API

Create an order (Command):
```bash
curl -X POST "http://localhost:8080/api/orders?customerId=CUST-101&amount=299.99"
```

Confirm an order (Command):
```bash
curl -X POST "http://localhost:8080/api/orders/<ORDER_ID>/confirm"
```

---

## Automated Verification & Tests

Run the complete test suite (unit tests and containerized integration tests):
```bash
./mvnw clean verify
```

### Included Test Suites
- **`OrderCommandServiceTest`:** Mockito unit tests validating event generation and dispatch to the event store.
- **`OrderProjectorTest`:** Mockito unit tests verifying projection state transitions from `PENDING` to `CONFIRMED`.
- **`CqrsApplicationTests`:** End-to-end integration tests using MockMvc and PostgreSQL Testcontainers validating command execution, event persistence, and projection read model updates.
