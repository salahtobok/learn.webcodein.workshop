# Refactoring a Legacy Spring Boot Monolith to Hexagonal Architecture

Complete companion repository for the technical article: [Refactoring a Legacy Spring Boot Monolith to Hexagonal Architecture](https://learn.webcodein.com/post/refactoring-legacy-spring-boot-to-hexagonal-architecture).

---

## Overview

In enterprise Java applications, layered architectures often degrade into tightly coupled monoliths where database entities leak into web controllers and business logic cannot be tested in isolation.

This project demonstrates a step-by-step refactoring journey from a traditional three-tier Spring Boot application to a clean **Hexagonal Architecture** (Ports and Adapters):

- **Legacy Architecture (`com.webcodein.workshop.legacy`):** Typical layered structure with coupled `User` JPA entity, repository, and service script.
- **Refactored Hexagonal Architecture (`com.webcodein.workshop.refactored`):**
  - **`domain`:** Pure Java 25 domain model with zero framework dependencies.
  - **`domain.port`:** Inbound and outbound port contracts (`UserRepositoryPort`).
  - **`application`:** Isolated use case services orchestrating business logic (`RegisterUserService`).
  - **`adapter.in`:** Driving REST web adapter (`UserWebAdapter`).
  - **`adapter.out`:** Driven persistence adapter wrapping Spring Data JPA (`UserRepositoryAdapter`, `UserJpaEntity`).

---

## Tech Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.0
- **Persistence:** Spring Data JPA with H2 in-memory database
- **Testing:** JUnit 5, AssertJ, ArchUnit 1.4.1, MockMvc

---

## Running the Tests

Execute the complete test suite including ArchUnit architectural fitness tests and MockMvc web integration tests:

```bash
./mvnw clean verify
```

### Included Test Suites
- **`HexagonalArchitectureTest`:** 4 ArchUnit 1.4.1 architectural fitness rules enforcing:
  1. `domainMustNotDependOnFrameworks`: Domain core has zero dependencies on Spring, JPA, or Jackson.
  2. `domainMustNotDependOnAdapters`: Inward dependency inversion ensuring the domain does not import adapters.
  3. `adaptersMustNotDependOnEachOther`: Complete decoupling between driving (inbound) and driven (outbound) adapters.
  4. `portsMustBeInterfaces`: Inbound and outbound ports are strictly defined as interfaces.
- **`ApplicationTests`:** Context loading and MockMvc slice tests verifying user registration API behavior.
