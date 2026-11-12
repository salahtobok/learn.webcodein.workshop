# Modern Local Development: Testcontainers & Docker Compose in Spring Boot 4

Companion workshop project for the post [Modern Local Development: Testcontainers & Docker Compose in Spring Boot 4](https://learn.webcodein.com/post/modern-local-development-testcontainers-docker-compose-spring-boot-4).

## Architecture Overview
This project demonstrates modern local development and automated testing workflows in **Spring Boot 4.0.0** and **Java 25**:
- **Local Development Loop (`compose.yaml`):** The `spring-boot-docker-compose` module automatically detects `compose.yaml` at application startup, provisions local containerized PostgreSQL 16 and Redis 7 services, and injects dynamic connection properties without manual Docker CLI commands.
- **Automated Integration Testing (`@ServiceConnection`):** `EmployeeRepositoryIntegrationTest` uses Testcontainers with Spring Boot 4's `@ServiceConnection` SPI to dynamically wire JDBC properties without `@DynamicPropertySource` boilerplate.
- **Reusable Singleton Testcontainers (`AbstractIntegrationTest`):** Demonstrates the static singleton container pattern where PostgreSQL is instantiated once across the test suite, drastically reducing test execution overhead.
- **ArchUnit Architectural Rules (`DockerComposeArchitectureTest`):** Enforces 5 strict architectural rules using ArchUnit 1.4.1, verifying interface contracts, entity annotations, package boundaries, and zero standard stream logging.

## Tech Stack
- **Java:** 25
- **Framework:** Spring Boot 4.0.0 (Spring Framework 7.0)
- **Data Persistence:** Spring Data JPA / Hibernate 7.1.8
- **Database:** PostgreSQL 16 Alpine
- **Container Orchestration:** Docker Compose & Testcontainers 2.0.2
- **Architecture Verification:** ArchUnit 1.4.1
- **Testing:** JUnit 5, AssertJ, Spring Boot Test

## Test Suite & Verification Contracts
The project includes 8 automated tests executing against real containerized infrastructure:
- `DockerComposeArchitectureTest` (5 ArchUnit rules):
  - `repositoriesMustBeInterfaces`: Validates all Spring Data repositories are interfaces.
  - `entitiesMustBeAnnotatedWithEntity`: Validates entity classes carry `@Entity`.
  - `noClassesShouldAccessStandardStreams`: Prohibits `System.out`/`System.err` in favor of SLF4J logging.
  - `noClassesShouldUseJavaUtilLogging`: Enforces standard logging facade.
  - `entitiesMustResideInEmployeePackage`: Guarantees domain packaging integrity.
- `EmployeeRepositoryIntegrationTest` (2 integration tests): Tests entity persistence and derived query lookup on ephemeral PostgreSQL via `@ServiceConnection`.
- `EmployeeSingletonContainerTest` (1 integration test): Tests entity counting against a shared singleton container instance.

## Build and Run Instructions

### Prerequisites
- JDK 25 installed and configured (`JAVA_HOME`).
- Maven 3.9+ (or use the root Maven wrapper).
- Docker Engine / Docker Desktop running locally.

### Local Development Mode
```bash
# Automatically boots PostgreSQL & Redis via compose.yaml and starts Spring Boot
mvn spring-boot:run
```

### Run Test Suite
```bash
# Executes all 8 unit, architecture, and Testcontainers integration tests
mvn clean test
```
