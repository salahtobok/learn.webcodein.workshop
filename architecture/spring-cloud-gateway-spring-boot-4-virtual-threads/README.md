# Spring Cloud Gateway Server MVC with Java 25 Virtual Threads in Spring Boot 4

Companion code repository for the article: **"Spring Cloud Gateway in Spring Boot 4: Virtual Threads MVC vs. Reactive WebFlux"**.

## Architecture Overview

This project demonstrates the transition from legacy reactive API gateways (Spring Cloud Gateway Reactive with WebFlux and Netty) to modern imperative API gateways (Spring Cloud Gateway Server MVC) powered by Java 25 Virtual Threads and Spring Boot 4.0.0.

Key architectural features:
- **Imperative Simplicity**: Standard synchronous blocking I/O programming model running on embedded Tomcat with `spring.threads.virtual.enabled=true`.
- **Token Relay Filter**: Propagates OAuth 2.0 Bearer JWT tokens downstream while stripping untrusted client headers.
- **Rate Limiter Filter**: In-memory token bucket rate limiting with HTTP 429 status and standard rate-limiting headers.
- **Graceful Fallback**: Centralized fallback endpoints returning structured JSON responses when downstream services are unreachable.
- **ArchUnit 1.4.1 Governance**: Automated architectural rules ensuring strict layer isolation and domain record immutability.

## Technology Stack

- **Java**: 25 (OpenJDK / Temurin)
- **Framework**: Spring Boot 4.0.0
- **Testing**: JUnit 5, AssertJ, Spring WebMvc Test (MockMvc), ArchUnit 1.4.1
- **Build**: Maven 3.9+

## Running Tests

Execute the full suite of unit, MockMvc slice, and ArchUnit architecture tests:

```bash
mvn test
```

Expected output:
```text
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Running the Application

```bash
./mvnw spring-boot:run
```

Verify gateway stats:
```bash
curl -i http://localhost:8080/admin/gateway/stats
```

Verify fallback endpoint:
```bash
curl -i http://localhost:8080/fallback/orders-service
```
