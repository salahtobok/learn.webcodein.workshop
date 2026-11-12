# Full-Stack Angular 22 Signals & Spring Boot 4 WebFlux Workshop

Companion code for the in-depth technical guide: [Full-Stack Masterclass: Angular 22 Signals & Spring Boot 4 Reactive APIs](https://learn.webcodein.com/post/angular22-springboot4-signals).

## Overview

This companion project demonstrates how to build, test, and containerize an end-to-end reactive application using **Spring Boot 4.0.0**, **Java 25**, **Spring Data R2DBC**, **PostgreSQL 16**, **Project Reactor**, and **Angular 22 Signals**. It illustrates non-blocking reactive persistence, streaming REST endpoints, Testcontainers integration testing, and ArchUnit 1.4.1 architectural fitness functions.

## Project Structure

```text
workshop/fullstack/angular22-springboot4-signals/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/webcodein/fullstack/reactive/
│   │   │   ├── ReactiveApplication.java      # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java       # Reactive REST endpoints (/api/tasks)
│   │   │   ├── model/
│   │   │   │   └── Task.java                 # R2DBC entity mapping
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java       # ReactiveCrudRepository interface
│   │   │   └── service/
│   │   │       └── TaskService.java          # Reactive business logic layer
│   │   └── resources/
│   │       ├── application.properties        # R2DBC & schema initialization config
│   │       └── schema.sql                    # PostgreSQL schema definition
│   └── test/
│       └── java/com/webcodein/fullstack/reactive/
│           ├── ReactiveArchitectureTest.java # ArchUnit 1.4.1 architectural rules
│           ├── controller/
│           │   └── TaskControllerIntegrationTest.java # WebTestClient & PostgreSQL Testcontainers
│           └── service/
│               └── TaskServiceUnitTest.java  # Reactor StepVerifier & Mockito unit tests
└── README.md
```

## Key Architectural Concepts

1. **Non-Blocking Reactive Pipeline**: End-to-end reactive stream from PostgreSQL over R2DBC through Netty and Spring WebFlux to Angular 22 client signals.
2. **Angular 22 Signals Integration**: Converting SSE / reactive JSON streams into fine-grained reactive state signals with `toSignal` without Zone.js overhead.
3. **Containerized Reactive Testing**: Integration testing with PostgreSQL 16 Testcontainers and `WebTestClient` with dynamic R2DBC connection properties.
4. **ArchUnit 1.4.1 Architecture Enforcement**: Automated verification of controller package boundaries, service annotations, repository interface rules, and controller-repository decoupling.

## Prerequisites

- **Java Development Kit (JDK)**: JDK 25
- **Apache Maven**: 3.9+ (or use the included `./mvnw` wrapper)
- **Docker**: Required for PostgreSQL Testcontainers and local Docker Compose

## Test Suite Verification

Run the complete test suite:

```bash
mvn clean test
# or
mvn -B -ntp verify
```

### Test Breakdown

- `TaskControllerIntegrationTest`: Full integration test using `WebTestClient` against real PostgreSQL 16 container via Testcontainers, verifying HTTP POST, HTTP GET, and reactive persistence.
- `TaskServiceUnitTest`: Unit tests verifying reactive service methods (`createTask`, `getAllTasks`) using Mockito and Project Reactor's `StepVerifier`.
- `ReactiveArchitectureTest`: Enforces 4 ArchUnit 1.4.1 architectural rules:
  - `@RestController` annotation enforcement on controller classes.
  - `@Service` annotation and package residence constraints on domain services.
  - Interface-only design for Spring Data R2DBC repositories.
  - Strict decoupling of controllers from repositories, routing all data access through services.

## Related Resources

- Live Article: [Full-Stack Masterclass: Angular 22 Signals & Spring Boot 4 Reactive APIs](https://learn.webcodein.com/post/angular22-springboot4-signals)
- Parent Repository: [learn.webcodein.com](https://github.com/salahtobok/learn.webcodein.com)
