# Hexagonal vs. Clean vs. Onion Architecture with Spring Boot 4

Companion code repository for the architectural guide: [Hexagonal Architecture vs. Clean Architecture vs. Onion Architecture](https://learn.webcodein.com/post/hexagonal-vs-clean-vs-onion-architecture).

This project implements the exact same business use case across three decoupled architectural paradigms side-by-side using **Spring Boot 4.0.0** and **Java 25**:
1. **Hexagonal Architecture (Ports and Adapters)** (`com.webcodein.architecture.hexagonal`)
2. **Clean Architecture (Concentric Onion Circles)** (`com.webcodein.architecture.clean`)
3. **Onion Architecture** (`com.webcodein.architecture.onion`)

---

## Architectural Comparison Matrix

| Aspect | Hexagonal Architecture | Clean Architecture | Onion Architecture |
|---|---|---|---|
| **Core Concept** | Inside (Domain) vs Outside (World) | Concentric Circles & Dependency Rule | Domain Model at Central Core |
| **Primary Abstraction** | Ports (Inbound & Outbound Interfaces) | Use Case Interactors & Boundaries | Domain Services & Repository Interfaces |
| **Delivery Decoupling** | Driving Adapters (Web, CLI, Queue) | Controllers, Presenters, Gateways | Presentation Layer (Controllers) |
| **Infrastructure Decoupling** | Driven Adapters (JPA, REST, Messaging) | Interface Adapters & Frameworks | Infrastructure Layer (Repositories) |

---

## Prerequisites

- **Java**: JDK 25+
- **Build Tool**: Maven 3.9+ (or use the included `./mvnw`)
- **Docker**: Optional for containerization

---

## Verification & Test Suite

The project includes 8 automated tests running on Java 25:
- **Architecture Application Tests (`ArchitectureApplicationTests`)**: Validates Spring context boot.
- **REST Integration Tests (`IntegrationTests`)**: Tests Hexagonal (`/hexagonal/orders`), Clean (`/clean/orders`), and Onion (`/onion/orders`) endpoints using MockMvc.
- **ArchUnit Boundary Governance (`ArchitectureArchUnitTest`)**: 4 automated architectural fitness rules using ArchUnit 1.4.1 enforcing strict domain isolation, entity independence, and interface port contracts.

```bash
# Run the full automated test suite
./mvnw clean test

# Run full build and package verification
./mvnw clean verify
```

---

## Running Locally

```bash
./mvnw spring-boot:run
```

Once running on port `8080`, test each architectural style:
```bash
# 1. Hexagonal Architecture Endpoint
curl -X POST http://localhost:8080/hexagonal/orders -H "Content-Type: application/json" -d '{"orderId": "hex-101", "amount": 99.50}'

# 2. Clean Architecture Endpoint
curl -X POST http://localhost:8080/clean/orders -H "Content-Type: application/json" -d '{"orderId": "clean-202", "amount": 149.00}'

# 3. Onion Architecture Endpoint
curl -X POST http://localhost:8080/onion/orders -H "Content-Type: application/json" -d '{"orderId": "onion-303", "amount": 199.99}'
```
