# Chaos Engineering with Spring Boot 4, Testcontainers, and Toxiproxy

This is the companion code for the Webcodein tutorial: [Chaos Engineering with Spring Boot 4, Testcontainers, and Toxiproxy](https://learn.webcodein.com/post/chaos-engineering-spring-boot-4-toxiproxy)

This project demonstrates how to simulate network failures using Toxiproxy and Testcontainers, and how to verify that a Spring Boot application recovers gracefully using `@Retryable` and `@Recover`.

## Prerequisites
- Java 25
- Maven 3.9+
- Docker

## Running the Application Locally via Docker

1. Package the application:
   ```bash
   ./mvnw clean package -DskipTests
   ```
2. Start the services (PostgreSQL and Spring Boot API):
   ```bash
   docker compose up --build -d
   ```
3. Test the API:
   ```bash
   curl -X POST "http://localhost:8080/api/orders?product=MacBook&quantity=1"
   ```

## Running the Tests

The integration tests will automatically spin up PostgreSQL and Toxiproxy via Testcontainers, inject a network failure (chaos), and verify that Spring Retry handles the failure gracefully.

```bash
./mvnw clean verify
```

## Structure
- `ChaosApplication.java`: Main Spring Boot application class, enabling Retry.
- `OrderService.java`: Service class using `@Retryable` to retry on network failures and `@Recover` for fallback.
- `ChaosEngineeringIntegrationTest.java`: Integration test injecting faults with Toxiproxy.
