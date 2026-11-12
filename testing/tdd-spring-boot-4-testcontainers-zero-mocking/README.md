# TDD with Spring Boot 4 and Testcontainers: Zero-Mocking Demo

This companion project demonstrates the **Zero-Mocking approach** to Test-Driven Development (TDD) using Spring Boot 4, Java 25, and Testcontainers. It tests against a real ephemeral PostgreSQL 16 database instead of brittle mocks or in-memory H2 databases.

Companion article: [TDD with Spring Boot 4 and Testcontainers: The Zero-Mocking Approach](https://learn.webcodein.com/post/tdd-spring-boot-4-testcontainers-zero-mocking).

---

## Prerequisites
- **JDK 25**
- **Docker Desktop** (running, for Testcontainers)
- **Maven 3.9+** (or use included `./mvnw`)

---

## Running the Automated Test Suite

Run the full automated test suite using the Maven wrapper:
```bash
./mvnw clean test
```

### Verified Test Contracts (8/8 Tests Pass):
1. `shouldProcessValidPayment`: Submits a valid payment JSON via MockMvc, persists into live PostgreSQL 16 container, and validates HTTP 200 with generated UUID and `PROCESSED` status.
2. `shouldRejectNegativeAmount`: Validates negative amount domain validation rejecting with HTTP 400 and descriptive error.
3. `shouldRejectDuplicateReference`: Validates idempotency constraint by submitting identical payment reference and asserting HTTP 400 rejection with `Payment reference already exists`.
4. `controllers_should_be_annotated_and_named`: ArchUnit enforces that all controllers have `@RestController` and `Controller` suffix.
5. `services_should_be_annotated_and_named`: ArchUnit enforces that all services have `@Service` and `Service` suffix.
6. `repositories_should_be_interfaces`: ArchUnit enforces that all repositories are interfaces.
7. `no_standard_streams`: ArchUnit forbids raw `System.out` / `System.err` calls in production code.
8. `no_java_util_logging`: ArchUnit enforces SLF4J logging across the entire codebase.

---

## Running Locally

Start PostgreSQL via Docker Compose:
```bash
docker compose up -d
```

Start the application:
```bash
./mvnw spring-boot:run
```

The application runs on `http://localhost:8080`.
