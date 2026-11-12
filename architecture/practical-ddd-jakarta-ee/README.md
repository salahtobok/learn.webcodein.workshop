# Practical Domain-Driven Design with Jakarta EE 11

Companion reference implementation for the technical guide:
**[Practical Domain-Driven Design with Jakarta EE 11](https://learn.webcodein.com/post/practical-ddd-jakarta-ee)**

This project demonstrates how to implement tactical Domain-Driven Design (DDD) patterns on modern Java 25 and Spring Boot 4 using Jakarta EE 11 specifications:
- **Jakarta REST (Jersey)** for RESTful boundary endpoints.
- **Jakarta Data 1.0.1** and **Hibernate 7.1.0** compile-time generated repositories (`@Repository` with `StatelessSession`).
- **ArchUnit 1.4.1** for compile-time architecture governance and DDD layering verification.
- **PostgreSQL 16 Testcontainers** for isolated, reproducible integration testing.

---

## Architecture Overview

The system models a document publishing lifecycle following strict DDD tactical patterns:

```
src/main/java/com/webcodein/documents/
├── domain/                         # Pure Domain Layer (Zero Framework Dependencies)
│   └── model/
│       ├── Document.java           # Aggregate Root: enforces lifecycle invariants
│       ├── DocumentId.java         # Value Object: type-safe identity
│       ├── DocumentTitle.java      # Value Object: self-validating title rules
│       ├── DocumentStatus.java     # Enum: DRAFT, PUBLISHED, ARCHIVED
│       └── DocumentRepository.java # Outbound Port: Jakarta Data Repository
├── application/                    # Application Service Layer
│   └── service/
│       └── DocumentService.java    # Use case orchestrator & transaction boundary
├── infrastructure/                 # Infrastructure Layer
│   └── persistence/
│       └── JakartaDataConfig.java  # Hibernate StatelessSession provider
└── interfaces/                     # User Interface / Adapter Layer
    └── rest/
        ├── DocumentResource.java   # Jakarta REST (Jersey) resource endpoint
        ├── CreateDocumentRequest.java
        └── DocumentResponse.java
```

---

## Technical Stack

| Component | Specification / Version |
| :--- | :--- |
| **Language** | Java 25 (OpenJDK 25) |
| **Framework** | Spring Boot 4.0.0 |
| **REST API** | Jakarta RESTful Web Services 3.1 (`spring-boot-starter-jersey`) |
| **Persistence** | Jakarta Data 1.0.1, Hibernate ORM 7.1.0 (`StatelessSession`) |
| **Database** | PostgreSQL 16 (via Testcontainers) |
| **Architecture Testing** | ArchUnit 1.4.1 (`archunit-junit5`) |
| **Integration Testing** | Testcontainers 1.20.4 & JUnit 5 |

---

## Test Suite (25 Tests)

The project includes an exhaustive 25-test suite covering domain logic, services, REST boundaries, database persistence, and structural architectural rules:

1. **Domain Tests (`DocumentTest.java` - 8 tests):**
   - Aggregate creation in `DRAFT` status with valid invariants.
   - Rejection of blank or malformed title value objects.
   - Rejection of publishing non-draft or archived documents.
   - Successful state transitions (`publish()`, `archive()`).
2. **Application Service Tests (`DocumentServiceTest.java` - 5 tests):**
   - Mocked repository interactions, entity lookup, and state persistence.
   - Exception handling on missing document lookup.
3. **REST Resource Tests (`DocumentResourceTest.java` - 5 tests):**
   - Jakarta REST (Jersey) HTTP status codes (`201 Created`, `200 OK`, `204 No Content`, `404 Not Found`).
   - JSON serialization and response mapping.
4. **Architecture Governance (`DocumentArchitectureTest.java` - 6 tests):**
   - Domain layer isolation (zero dependency on Spring, Jersey, or Hibernate).
   - Value Objects immutability and package containment.
   - Repository naming conventions and port placement.
   - Resource interface isolation.
5. **Database Integration (`ApplicationTests.java` - 1 test):**
   - Full Spring Boot context bootstrap with real PostgreSQL 16 in Testcontainers.

---

## Quick Start

### Prerequisites
- JDK 25 installed and configured (`JAVA_HOME`).
- Docker running (for PostgreSQL Testcontainers).

### Build and Run Tests
```bash
mvn clean verify
```

### Run Locally with Docker Compose
```bash
# 1. Start PostgreSQL 16
docker compose up -d

# 2. Run the application
mvn spring-boot:run

# 3. Create a draft document
curl -s -X POST http://localhost:8080/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"Jakarta EE 11 DDD Architecture"}'

# 4. Fetch the document by ID (replace <ID>)
curl -s http://localhost:8080/documents/<ID>

# 5. Publish the document
curl -s -X POST http://localhost:8080/documents/<ID>/publish

# 6. Archive the document
curl -s -X POST http://localhost:8080/documents/<ID>/archive
```
