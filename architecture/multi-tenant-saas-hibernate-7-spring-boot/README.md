# Multi-Tenant SaaS with Hibernate 7, Spring Boot 4, and PostgreSQL RLS

Complete companion repository for the technical article: [Building a Secure Multi-Tenant SaaS Backend with Spring Boot 4, Hibernate 7, and PostgreSQL RLS](https://learn.webcodein.com/post/multi-tenant-saas-hibernate-7-spring-boot).

---

## Overview

In multi-tenant SaaS applications, isolating customer records while maintaining low infrastructure overhead is critical. This project implements a **Defense-in-Depth** multi-tenancy architecture combining application-level query rewriting with database kernel enforcement:

1. **Application-Level Isolation:** Hibernate 7 `@TenantId` automatically appends tenant criteria to JPA and Criteria queries.
2. **Database-Level Isolation:** PostgreSQL 16 Row-Level Security (RLS) policies enforce mathematical record isolation inside the database engine.
3. **Session Variable Bridge:** Spring AOP executes `SET LOCAL app.current_tenant` on the active transactional connection upon entry.
4. **Connection Pooling Safety:** HikariCP connection reuse is guaranteed safe because `SET LOCAL` is automatically reset upon transaction `COMMIT`.

---

## Tech Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.0
- **ORM / Persistence:** Hibernate 7.1 with Jakarta Persistence 3.2
- **Database:** PostgreSQL 16 (via Testcontainers and Docker)
- **Database Migrations:** Flyway
- **Testing:** JUnit 5, AssertJ, ArchUnit 1.4.1, Testcontainers 2.0.2

---

## Running the Application Locally

### 1. Requirements
- JDK 25 installed and configured on PATH
- Docker daemon running (for Testcontainers)

### 2. Automated Test Verification
Run the complete test suite (architecture rules and containerized integration tests):
```bash
./mvnw clean verify
```

### Included Test Suites
- **`SaasArchitectureTest`:** ArchUnit 1.4.1 architectural fitness rules enforcing controller package isolation, controller independence, entity model residency, and repository interface definitions.
- **`SaasApplicationTests`:** End-to-end integration tests using containerized PostgreSQL 16 validating that `tenant-a` can never read or mutate records belonging to `tenant-b`.

---

## Features Demonstrated

1. **Hibernate 7 `@TenantId`:** Declarative tenant identification without manual SQL filter boilerplate.
2. **PostgreSQL RLS Policies:** Native kernel-level row filtering preventing cross-tenant leaks even under raw SQL execution.
3. **Spring AOP Session Scoping:** Transaction-scoped `SET LOCAL` session execution preventing connection pool pollution.
4. **Full Testcontainers Isolation:** Automated verification with real PostgreSQL 16 database containers.
