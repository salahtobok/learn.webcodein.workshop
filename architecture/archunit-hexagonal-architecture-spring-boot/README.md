# Enforcing Hexagonal Architecture with ArchUnit

This project demonstrates how to enforce Hexagonal Architecture rules in a Spring Boot 4 application using ArchUnit.

## Prerequisites
- Java 25
- Maven 3.9+
- Docker

## Running Locally

1. Build the project and run ArchUnit tests:
   ```bash
   ./mvnw clean verify
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Running via Docker

1. Build the Docker image:
   ```bash
   docker build -t archunit-hexagonal .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 archunit-hexagonal
   ```

## Testing the API

```bash
curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"product": "Spring Boot ArchUnit Guide", "quantity": 1}'
```
