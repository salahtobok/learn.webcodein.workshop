# Transactional Outbox Pattern with Spring Boot 4

This is the companion codebase for the article: **Advanced Kafka: The Transactional Outbox Pattern with Spring Boot 4**.

## Prerequisites
- Java 25
- Docker (for Testcontainers and Compose)
- Maven

## Running the Application

1. **Start dependencies (Kafka & PostgreSQL)**
```bash
docker compose up -d
```

2. **Run the Spring Boot application**
```bash
./mvnw spring-boot:run
```

## Running Tests

The project includes integration tests that automatically spin up Kafka and PostgreSQL using Testcontainers.

```bash
./mvnw clean verify
```
