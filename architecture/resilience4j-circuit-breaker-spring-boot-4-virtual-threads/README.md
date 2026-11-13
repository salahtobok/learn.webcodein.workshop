# Resilience4j Circuit Breaker & Retry with Java 25 Virtual Threads in Spring Boot 4

Companion code repository for the article: [Building Fault-Tolerant Microservices: Resilience4j Circuit Breaker & Retry with Spring Boot 4 and Java 25 Virtual Threads](https://learn.webcodein.com/post/resilience4j-circuit-breaker-spring-boot-4-virtual-threads).

---

## 1. Architectural Overview

This project demonstrates production-grade microservice fault tolerance using **Resilience4j**, **Spring Boot 4.0.0**, and **Java 25 Virtual Threads**.

### Key Architectural Concepts:
- **Zero-Cost Bulkheading:** Replaces legacy thread-pool bulkheads with `SemaphoreBulkhead`. Java 25 Virtual Threads eliminate the need for heavy dedicated OS thread pools.
- **Sliding Window Circuit Breaking:** Evaluates error rates over a 5-call sliding window. Automatically trips from `CLOSED` to `OPEN` when the failure rate exceeds 50%.
- **Exponential Retry & Graceful Fallback:** Catches `CallNotPermittedException` and remote HTTP errors, routing requests into a cached degraded response (`QUEUED_DEGRADED`) rather than failing user transactions.
- **Automated Architectural Fitness (ArchUnit 1.4.1):** Enforces strict package residency, controller decoupling, and immutable domain records.

---

## 2. Technology Stack

- **Java:** JDK 25
- **Framework:** Spring Boot 4.0.0
- **Resilience Engine:** Resilience4j 2.2.0 (`circuitbreaker`, `retry`, `bulkhead`, `micrometer`)
- **Testing:** JUnit 5, AssertJ, Spring Boot Test (`MockMvc`), ArchUnit 1.4.1

---

## 3. Running & Verifying Locally

### Maven Verification
To compile the application and execute all unit, slice, and architectural fitness tests:

```bash
mvn clean test
```

### Running the Application
```bash
mvn spring-boot:run
```

---

## 4. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/payments/process` | Executes payment via decorated Resilience4j pipeline. |
| `GET` | `/api/payments/circuit-breaker-status` | Returns real-time circuit breaker metrics and state. |
| `POST` | `/api/payments/simulate-failure?enabled=true` | Toggles remote payment gateway failure simulation. |
| `POST` | `/api/payments/reset` | Resets circuit breaker metrics to initial CLOSED state. |
