# DDD Layers Explained: Code Structure in Spring Boot 4

Companion workshop repository for the tutorial: [DDD Layers Explained: Where Does Each Piece of Code Go? | Spring Boot Guide](https://learn.webcodein.com/post/ddd-layers-explained-code-structure-spring-boot).

This project demonstrates a production-grade 4-layer Domain-Driven Design (DDD) architecture implemented in **Spring Boot 4.0.0** and **Java 25**, strictly adhering to the **Dependency Inversion Principle (DIP)** and enforced via **ArchUnit** architecture tests.

---

## Architecture Overview

```
com.webcodein.workshop.dddlayersexplainedcodestructurespringboot
├── presentation/         # Controllers, Request/Response DTOs (Inbound HTTP Adapter)
├── application/          # Application Services, Commands, Queries (Use Case Orchestration)
├── domain/               # Aggregates, Entities, Value Objects, Domain Events, Repository Interfaces
└── infrastructure/       # JPA Entities, Spring Data Repositories, Database Adapters (Outbound Adapter)
```

### Dependency Inversion Rules
1. **Domain Layer**: The heart of the software. Contains zero framework dependencies (no Spring, no JPA, no Web annotations).
2. **Application Layer**: Depends strictly on the Domain Layer. Orchestrates transactions and executes use cases.
3. **Infrastructure Layer**: Outer adapter implementing Domain Repository interfaces. Depends inward on Domain.
4. **Presentation Layer**: Outer HTTP adapter mapping REST payloads to application commands.

---

## Prerequisites

- **Java 25** (JDK 25)
- **Maven 3.9+** (or use the provided `./mvnw` wrapper)

---

## Running the Test Suite

Run the full suite of unit, MockMvc slice, Spring Boot JPA integration, and ArchUnit architectural governance tests:

```bash
./mvnw clean test
```

### Test Suite Structure
- `domain/ProductTest`: Unit test validating aggregate business invariants (`reduceStock`, `InsufficientStockException`).
- `domain/DomainValueObjectsTest`: Unit tests validating immutability and domain boundaries of `Money`, `ProductName`, and `ProductId`.
- `application/ProductApplicationServiceTest`: Mockito unit test verifying application orchestration and repository interactions.
- `presentation/ProductControllerTest`: `@WebMvcTest` slice test verifying HTTP REST contract, status codes, and JSON serialization.
- `infrastructure/DddApplicationIntegrationTest`: `@SpringBootTest` end-to-end integration test exercising the full 4-layer lifecycle with in-memory H2 database.
- `DddArchitectureTest`: **ArchUnit** test suite verifying that no layer violates inbound dependency inversion rules, and that the domain remains 100% free of Spring Framework and Jakarta Persistence dependencies.

---

## Running the Application Locally

```bash
./mvnw spring-boot:run
```

Once started, test product creation via cURL:

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mechanical Keyboard Pro",
    "price": 189.95,
    "currency": "USD",
    "initialStock": 25
  }'
```
