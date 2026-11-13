# Resilient Event Streaming with Kafka Dead-Letter Topics (DLT) & Non-Blocking Retries in Spring Boot 4

Companion code repository for the article: **"Resilient Event Streaming: Kafka Dead-Letter Topics (DLT) & Non-Blocking Retries in Spring Boot 4"**.

## Architecture Overview

This project demonstrates how to build resilient, fault-tolerant event-driven microservices using Spring Boot 4.0.0 and Java 25. It eliminates traditional head-of-line blocking by implementing non-blocking multi-topic retries and Dead-Letter Topics (DLT).

Key architectural features:
- **Non-Blocking Multi-Topic Retries**: Failing messages are forwarded to delayed retry topics (`orders-retry-1000`, `orders-retry-2000`, `orders-retry-4000`), allowing healthy messages on the main partition to proceed without latency spikes.
- **Poison Pill Isolation**: Non-transient errors (e.g. malformed JSON, corrupted data) bypass retry loops and route immediately to `orders-dlt` to prevent wasted broker compute.
- **DLT Reconciliation & Replay API**: Administrative REST endpoints to inspect dead-letter payloads, view root-cause exceptions, and re-inject repaired records into the primary stream.
- **ArchUnit 1.4.1 Governance**: Automated architectural rules ensuring package boundary compliance and record immutability.

## Technology Stack

- **Java**: 25 (OpenJDK / Temurin)
- **Framework**: Spring Boot 4.0.0
- **Testing**: JUnit 5, AssertJ, Spring WebMvc Test (MockMvc), ArchUnit 1.4.1
- **Build**: Maven 3.9+

## Running Tests

Execute all unit, MockMvc slice, and ArchUnit architecture tests:

```bash
mvn test
```

Expected output:
```text
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Running the Application

```bash
./mvnw spring-boot:run
```

Simulate processing an event:
```bash
curl -i -X POST http://localhost:8080/admin/dlt/simulate \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-101","customerId":"CUST-1","amount":99.95,"eventType":"ORDER_CREATED","retryCount":0}'
```

Inspect Dead-Letter Topic:
```bash
curl -i http://localhost:8080/admin/dlt/messages
```
