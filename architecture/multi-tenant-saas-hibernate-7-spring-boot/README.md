# Multi-Tenant SaaS with Hibernate 7 and PostgreSQL RLS

This is the companion code for the tutorial on building a secure, shared-database multi-tenant SaaS application using **Spring Boot 4**, **Hibernate 7**, and **PostgreSQL Row-Level Security (RLS)**.

## Requirements
- Java 25
- Docker (for Testcontainers)

## Running the Tests
This project uses Testcontainers to spin up a PostgreSQL instance automatically.

```bash
mvnw clean verify
```

## Features Demonstrated
1. **Application-Level Multi-Tenancy**: Using Hibernate 7's `@TenantId` annotation for seamless query filtering.
2. **Defense-in-Depth with PostgreSQL**: Implementing Row-Level Security policies via Flyway migrations.
3. **Spring AOP for Session Variables**: Using a custom Aspect to execute `SET LOCAL app.current_tenant` inside the active transactional connection to bind RLS policies.
