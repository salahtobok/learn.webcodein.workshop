# Chaos Engineering with Spring Boot 4, Testcontainers, and Toxiproxy

This is the companion code for the Webcodein tutorial: [Chaos Engineering with Spring Boot 4, Testcontainers, and Toxiproxy](https://learn.webcodein.com/post/chaos-engineering-spring-boot-4-toxiproxy).

This project demonstrates how to simulate network failures using Toxiproxy and Testcontainers, and how to verify that a Spring Boot 4 application recovers gracefully using `@Retryable` and `@Recover`.

---

## Prerequisites
- **JDK 25**
- **Maven 3.9+** (or use included `./mvnw`)
- **Docker Desktop** (running, for Testcontainers)

---

## Running the Automated Test Suite

The integration tests automatically spin up PostgreSQL 16 and Toxiproxy 2.9.0 via Testcontainers, inject dynamic network failures (bandwidth cuts, latency), and verify that Spring Retry handles the failure gracefully.

```bash
./mvnw clean test
```

### Verified Test Contracts (7/7 Tests Pass):
1. `shouldSuccessfullyPlaceOrderWhenDatabaseIsHealthy`: Verifies end-to-end order processing over the Toxiproxy bridge when network is operating at full speed (HTTP 200 OK, `status: PENDING`).
2. `shouldRecoverAndFailWhenDatabaseLatencyIsTooHigh`: Dynamically injects a zero-bandwidth toxic on Toxiproxy, asserts that Spring Retry executes retries with backoff, trips socket timeout (`socketTimeout=2s`), triggers `@Recover`, and returns HTTP 503 Service Unavailable with `FAILED_NETWORK_ISSUE`.
3. `controllers_should_be_annotated_and_named`: ArchUnit enforces `@RestController` and `Controller` naming convention.
4. `services_should_be_annotated_and_named`: ArchUnit enforces `@Service` and `Service` naming convention.
5. `repositories_should_be_interfaces`: ArchUnit enforces that all repository classes are interfaces.
6. `no_standard_streams`: ArchUnit forbids raw `System.out` / `System.err` calls.
7. `no_java_util_logging`: ArchUnit enforces SLF4J logging across the entire codebase.

---

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

---

## Structure
- `ChaosApplication.java`: Main Spring Boot application class, enabling Retry (`@EnableRetry`).
- `OrderService.java`: Service class using `@Retryable` to retry on network failures and `@Recover` for fallback.
- `OrderController.java`: REST controller exposing `/api/orders`.
- `ChaosEngineeringIntegrationTest.java`: Integration test injecting faults with Toxiproxy.
- `ChaosArchitectureTest.java`: ArchUnit architectural governance suite.
