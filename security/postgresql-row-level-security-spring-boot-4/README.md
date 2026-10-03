# PostgreSQL Row-Level Security in Spring Boot 4

This workshop project demonstrates how to push Access Control (ACL) down to the PostgreSQL database engine using Row-Level Security (RLS) in a Spring Boot application.

## Prerequisites
- Java 25
- Docker (for Testcontainers and Compose)

## Architecture
The application uses a Spring AOP Aspect (`RlsAspect.java`) to intercept all calls to the database and injects the current authenticated user's ID into the PostgreSQL connection session (`SET LOCAL rls.tenant_id = '...'`). The database physically filters out any rows that do not belong to that tenant.

## Running Locally (Maven)
You can run the application directly using the Maven wrapper. Testcontainers will automatically spin up a PostgreSQL database.

```bash
./mvnw spring-boot:run
```

## Building and Running with Docker Compose
To run the full stack (PostgreSQL + Spring Boot Application):

```bash
docker compose up --build
```

## Testing
To run the integration tests (which verify the RLS data isolation):

```bash
./mvnw clean verify
```
