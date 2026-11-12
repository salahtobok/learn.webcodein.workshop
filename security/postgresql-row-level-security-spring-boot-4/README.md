# PostgreSQL Row-Level Security in Spring Boot 4

Companion code repository for the article: [High-Performance Multi-Tenant ACLs: PostgreSQL 16 Row-Level Security with Spring Boot 4](https://learn.webcodein.com/post/postgresql-row-level-security-spring-boot-4).

This workshop project demonstrates how to enforce multi-tenant Access Control Lists (ACLs) directly within the PostgreSQL 16 database engine using Row-Level Security (RLS) and Spring Boot 4 on Java 25.

## Overview & Architecture

Instead of filtering data in application-level queries (e.g., `WHERE tenant_id = :tenantId`), this project delegates security enforcement directly to PostgreSQL:

1. **AOP Session Interception (`RlsAspect.java`)**: Intercepts data repository operations and sets the session-scoped variable `SET LOCAL rls.tenant_id = :tenantId` on the active connection.
2. **PostgreSQL RLS Policies (`schema.sql`)**: Physical database policies (`USING (tenant_id = current_setting('rls.tenant_id'))`) restrict `SELECT`, `UPDATE`, and `DELETE` queries at the storage engine level.
3. **Application Layer (`DocumentController.java`)**: Calls `documentRepository.findAll()` without manual tenant clauses. The database engine guarantees zero cross-tenant data leakage.
4. **Architectural Governance (ArchUnit 1.4.1)**: Verifies package isolation, REST controller annotations, repository interfaces, and AOP aspect structures.

## Tech Stack

- **Java**: 25
- **Framework**: Spring Boot 4.0.0
- **Database**: PostgreSQL 16
- **Security**: Spring Security 7 / Spring Data JPA 4
- **Testing**: Testcontainers PostgreSQL, MockMvc, JUnit 5, AssertJ, ArchUnit 1.4.1

## Prerequisites

- JDK 25 installed
- Docker running (for PostgreSQL Testcontainers or Docker Compose)
- Maven 3.9+ (or use the included `./mvnw`)

## Running the Application Locally

Run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or using Docker Compose:

```bash
docker compose up --build
```

## Running the Test Suite

Run the full test suite (including Testcontainers PostgreSQL RLS integration, controller slices, domain unit tests, and ArchUnit rules):

```bash
mvn clean test
```

## Test Structure

- `RlsDemoApplicationTests`: Testcontainers PostgreSQL 16 integration verifying multi-tenant isolation and MockMvc integration.
- `DocumentControllerTest`: MockMvc slice testing REST endpoints and security principal propagation.
- `DocumentTest`: Domain unit tests verifying entity construction and property mutations.
- `RlsArchitectureTest`: ArchUnit 1.4.1 rules enforcing entity placement, REST controller annotations, interface-driven repositories, and AOP aspect compliance.
