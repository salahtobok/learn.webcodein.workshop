# Spring Security ACL Demo

This project demonstrates how to implement Domain Object Security (row-level security) in Spring Boot 4 using the Spring Security ACL module and PostgreSQL.

## Prerequisites
- Java 25
- Docker (for Testcontainers and Compose)

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
To run the integration tests (which verify ACL permission constraints):

```bash
./mvnw clean verify
```
