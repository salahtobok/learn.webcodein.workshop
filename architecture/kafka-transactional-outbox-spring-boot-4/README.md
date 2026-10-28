# Transactional Outbox Pattern with Spring Boot 4 and Apache Kafka

Complete companion repository for the technical article: [Advanced Kafka: The Transactional Outbox Pattern with Spring Boot 4](https://learn.webcodein.com/post/kafka-transactional-outbox-spring-boot-4).

---

## Overview

In distributed architectures, writing to an ACID database and publishing a message to Apache Kafka are two disconnected operations. A network failure between these steps creates the **Dual Write Problem**, leaving data inconsistent across services.

This project implements the **Transactional Outbox Pattern** in Spring Boot 4 and Java 25:
1. **Atomic Persistence:** Saves business entities (`Order`) and event payloads (`OutboxEvent`) in the same PostgreSQL transaction.
2. **Polling Publisher:** Asynchronously polls pending events and transmits them to Kafka with strict in-order guarantees.
3. **Consumer Verification:** Listens to topic `order-events` to ensure end-to-end event propagation.

---

## Tech Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.0
- **Database:** PostgreSQL 16 (via Testcontainers and Docker Compose)
- **Message Broker:** Apache Kafka 3.8.0 Native (via Testcontainers and Docker Compose)
- **Testing:** JUnit 5, AssertJ, Mockito, Awaitility, Testcontainers 2.0.2

---

## Running the Application Locally

### 1. Start Infrastructure
Start local PostgreSQL and Kafka instances using Docker Compose:
```bash
docker compose up -d
```

### 2. Run Spring Boot Application
```bash
./mvnw spring-boot:run
```

### 3. Create an Order via REST API
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust-1001",
    "productCode": "LAPTOP-PRO-16",
    "quantity": 1,
    "totalAmount": 1999.99
  }'
```

---

## Automated Verification & Tests

Run the complete test suite (unit tests and containerized integration tests):
```bash
./mvnw clean verify
```

### Included Test Suites
- **`OrderServiceTest`:** Fast Mockito unit tests validating transactional boundaries and JSON serialization failure handling.
- **`OutboxApplicationTests`:** Context loading verification with containerized PostgreSQL and Kafka instances.
- **`TransactionalOutboxIntegrationTest`:** End-to-end integration tests using Testcontainers validating single-transaction persistence, polling publisher event transmission, and Kafka consumer delivery.
