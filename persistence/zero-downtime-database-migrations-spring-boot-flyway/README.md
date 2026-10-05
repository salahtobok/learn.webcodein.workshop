# Zero-Downtime Database Migrations with Spring Boot 4 and Flyway

This workshop project demonstrates how to use the Expand and Contract pattern with Flyway to rename/merge columns without downtime in a Spring Boot application.

## Prerequisites
- Java 25
- Docker (for Testcontainers)

## Running the application locally
The application uses Spring Boot's Docker Compose support. You can start it locally using:
```bash
./mvnw spring-boot:run
```

## Running the tests
Tests use Testcontainers to spin up a PostgreSQL database and verify that the Flyway migrations correctly migrate the existing dummy data to the new schema.
```bash
./mvnw clean verify
```

## Running with Docker
```bash
docker-compose up --build
```

## Companion Tutorial
Read the full step-by-step tutorial on [learn.webcodein.com](https://learn.webcodein.com/post/zero-downtime-database-migrations-spring-boot-flyway).
