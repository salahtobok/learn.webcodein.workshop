# Spring Security ACL Demo

This project demonstrates how to implement Domain Object Security (row-level access control) in Spring Boot 4 using the Spring Security ACL module and PostgreSQL 16.

For the full detailed walkthrough and architectural explanations, see the companion guide:  
**[Mastering Spring Security ACL: Domain Object Security in Spring Boot 4](https://learn.webcodein.com/post/spring-security-acl-domain-object-security-spring-boot-4)**

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
To run the integration tests (which verify ACL permission constraints and multi-user isolation):

```bash
./mvnw clean verify
```
