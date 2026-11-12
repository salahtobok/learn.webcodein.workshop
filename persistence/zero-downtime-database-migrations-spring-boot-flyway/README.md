# Zero-Downtime Database Migrations with Spring Boot 4 and Flyway

Companion reference implementation for the technical guide:
**[Zero-Downtime Database Migrations with Spring Boot 4 and Flyway](https://learn.webcodein.com/post/zero-downtime-database-migrations-spring-boot-flyway)**

This project demonstrates how to implement zero-downtime relational database schema migrations using **Spring Boot 4.0.0**, **Flyway**, **PostgreSQL 16**, and the **Expand and Contract (Parallel Run) pattern**.

---

## The Expand and Contract Pattern

Directly renaming or deleting database columns causes downtime during blue/green or rolling Kubernetes deployments because old and new application versions run concurrently against the same database.

This project implements the canonical four-step migration pipeline:

1. **Phase 1: Expand (V2)**: Add the new column (`full_name`) as nullable without removing old columns (`first_name`, `last_name`).
2. **Phase 2: Transition & Backfill (V3)**: Migrate existing data in batches and configure dual-writing / fallback mapping in application logic.
3. **Phase 3: Verify**: Deploy the new application version reading and writing exclusively to `full_name`.
4. **Phase 4: Contract (V4)**: Drop deprecated columns (`first_name`, `last_name`) once all old application instances are retired.

---

## Technical Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java (OpenJDK) | 25 |
| **Framework** | Spring Boot | 4.0.0 |
| **Migration Tool** | Flyway Core & Flyway PostgreSQL | 11.x (managed) |
| **Persistence** | Spring Data JPA / Hibernate ORM | 7.1.x |
| **Database** | PostgreSQL | 16 (via Testcontainers) |
| **Testing** | JUnit 5, MockMvc, ArchUnit, Testcontainers | 1.4.1 (ArchUnit), 2.0.2 (Testcontainers) |

---

## Test Suite (25 Tests)

The comprehensive test suite verifies every layer of the application:

1. **Entity Unit Tests (`CustomerTest.java` - 5 tests):**
   - Constructor invariant enforcement (rejects null/blank email and full name).
   - Domain validation during updates (`setFullName`).
2. **DTO Record Tests (`CustomerRequestTest.java` - 3 tests):**
   - Compact constructor validation for REST request payloads.
3. **Service Logic Tests (`CustomerServiceTest.java` - 4 tests):**
   - Mocked repository interactions, CRUD orchestration, and exception handling.
4. **WebMvc Slice Tests (`CustomerControllerTest.java` - 4 tests):**
   - HTTP status code mapping (`201 Created`, `200 OK`, `404 Not Found`).
   - JSON payload serialization and deserialization.
5. **Architecture Governance (`FlywayArchitectureTest.java` - 5 tests):**
   - ArchUnit structural rules: controller-to-service isolation, repository interfaces, entity annotations.
6. **Database Migration Integration Tests (`CustomerRepositoryTest.java` - 4 tests):**
   - Execution of all 4 Flyway migration scripts on a real PostgreSQL 16 Testcontainer.
   - Verification of backward-compatible data migration and subsequent CRUD operations.

---

## Quick Start

### Prerequisites
- Java 25 installed and configured.
- Docker running (for PostgreSQL Testcontainers).

### Build and Run Tests
```bash
mvn clean verify
```

### Run Locally with Docker Compose
```bash
# 1. Start PostgreSQL 16
docker compose up -d

# 2. Run the Spring Boot application
mvn spring-boot:run

# 3. Create a new customer
curl -s -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"email":"ada@example.com","fullName":"Ada Lovelace"}'

# 4. Fetch the customer
curl -s http://localhost:8080/api/customers/1
```
