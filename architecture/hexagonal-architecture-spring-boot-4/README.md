# Clean Architecture: Implementing Hexagonal (Ports & Adapters) in Spring Boot 4

Companion code repository for the architectural guide: [Clean Architecture: Implementing Hexagonal (Ports & Adapters) in Spring Boot 4](https://learn.webcodein.com/post/clean-architecture-hexagonal-ports-adapters-spring-boot-4).

This project demonstrates a production-ready implementation of **Hexagonal Architecture** (also known as Ports and Adapters) using **Spring Boot 4.0.0**, **Java 25**, and **Hibernate 7.1**. The core business domain is completely isolated from web protocols and database frameworks.

---

## Architectural Package Structure

```
com.webcodein.bank
├── domain/                               # Core Domain Layer (Pure Java, zero framework dependencies)
│   ├── model/                            # Domain models & invariants
│   │   ├── Account.java                  # Aggregate root with deposit/withdraw invariants
│   │   ├── AccountId.java                # Strongly typed value object
│   │   └── InsufficientFundsException.java
│   ├── port/
│   │   ├── in/                           # Inbound / Driving Ports (Use Cases)
│   │   │   └── TransferMoneyUseCase.java
│   │   └── out/                          # Outbound / Driven Ports (SPI)
│   │       ├── LoadAccountPort.java
│   │       └── UpdateAccountStatePort.java
│   └── service/                          # Pure Java Domain Service
│       └── TransferMoneyService.java     # Use case orchestrator (no @Service annotation)
├── adapter/                              # Infrastructure Adapters
│   ├── in/
│   │   └── web/                          # Driving Web Adapter
│   │       ├── TransferController.java   # Spring MVC REST Controller (@RestController)
│   │       └── TransferRequest.java      # HTTP Request DTO record
│   └── out/
│       └── persistence/                  # Driven Persistence Adapter
│           ├── AccountJpaEntity.java     # Relational JPA entity (@Table)
│           ├── AccountMapper.java        # Pure mapping between Domain & JPA
│           ├── AccountPersistenceAdapter.java # Implements LoadAccountPort & UpdateAccountStatePort
│           └── SpringDataAccountRepository.java # Spring Data JPA repository
└── configuration/
    └── DomainConfig.java                 # IoC Factory Bean: Wires pure POJO Domain Service
```

---

## Key Architectural Principles

1. **Inverted Dependency Graph**: Dependencies flow inward toward the domain core. The domain layer has zero imports from `org.springframework.*` or `jakarta.persistence.*`.
2. **Ports as Strict Contracts**: Inbound ports (`TransferMoneyUseCase`) expose what the domain can do. Outbound ports (`LoadAccountPort`, `UpdateAccountStatePort`) declare what the domain needs from external systems.
3. **External Spring IoC Wiring**: The domain service (`TransferMoneyService`) is instantiated in `DomainConfig` via standard `@Bean` methods, keeping the domain 100% testable without Spring application contexts.

---

## Prerequisites

- **Java**: JDK 25+
- **Build Tool**: Maven 3.9+ (or use the included `./mvnw`)
- **Docker**: Optional (H2 in-memory database used by default for local execution and tests)

---

## Verification & Test Suite

The project includes 12 automated unit and integration tests across all layers:

```bash
# Execute the full test suite
mvn clean test

# Execute package and verification
mvn clean verify
```

### Test Breakdown

1. **Domain Unit Tests (`AccountTest`)**: Verifies deposit, withdraw invariants, and balance boundary validations without frameworks.
2. **Domain Service Unit Tests (`TransferMoneyServiceTest`)**: Verifies money transfer logic using lightweight in-memory test doubles (no mocks or databases).
3. **Web Adapter Integration Tests (`TransferControllerTest`)**: Uses `@WebMvcTest` and `MockMvc` with `@MockitoBean` to test HTTP 200 OK and HTTP 400 Bad Request responses.
4. **Persistence Adapter Integration Tests (`AccountPersistenceAdapterTest`)**: Tests JPA mapping, loading, updating, and entity lifecycle in H2.
5. **End-to-End Integration Tests (`BankApplicationTests`)**: Validates full application context startup and end-to-end REST transfer flow.

---

## Running the Application Locally

```bash
mvn spring-boot:run
```

The application starts on port `8080`.

### Seed and Transfer Test via cURL

```bash
# Execute transfer between accounts
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "source": 1,
    "target": 2,
    "amount": 75.00
  }'
```

---

## Published Guide

For the complete technical breakdown, trade-off analysis, and comparison matrices, read the full article:  
👉 [Clean Architecture: Implementing Hexagonal (Ports & Adapters) in Spring Boot 4](https://learn.webcodein.com/post/clean-architecture-hexagonal-ports-adapters-spring-boot-4)
