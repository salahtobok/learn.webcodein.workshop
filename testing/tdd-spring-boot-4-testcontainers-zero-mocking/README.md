# TDD with Spring Boot 4 and Testcontainers: Zero-Mocking Demo

This companion project demonstrates the **Zero-Mocking approach** to Test-Driven Development (TDD) using Spring Boot 4, Java 25, and Testcontainers. It tests against a real ephemeral PostgreSQL 16 database instead of brittle mocks or in-memory H2 databases.

## Prerequisites
- Java 25 or newer
- Docker Desktop (for Testcontainers)

## Running Tests
Run the integration tests using the Maven wrapper:
```bash
./mvnw clean test
```
The test suite spins up a real PostgreSQL container via `@ServiceConnection`, executes HTTP requests with `MockMvc`, and validates database persistence without mocks.

## Running Locally
Start PostgreSQL via Docker Compose:
```bash
docker compose up -d
```

Start the application:
```bash
./mvnw spring-boot:run
```

## Companion Tutorial
Read the full article on [learn.webcodein.com](https://learn.webcodein.com/post/tdd-spring-boot-4-testcontainers-zero-mocking).
