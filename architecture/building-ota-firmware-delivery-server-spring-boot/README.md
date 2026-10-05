# Building an OTA Firmware Delivery Server with Spring Boot

This is the companion code for the tutorial [Building an OTA Firmware Delivery Server in Spring Boot](https://learn.webcodein.com/post/building-ota-firmware-delivery-server-spring-boot).

## Overview
This project demonstrates how to build a robust Over-The-Air (OTA) firmware delivery server using Spring Boot 4, Java 25, and PostgreSQL. It features:
- File chunking and resumable downloads using HTTP 206 Partial Content.
- Firmware version tracking in a PostgreSQL database using Spring Data JPA.
- Integration tests using Testcontainers.

## Running the Application

### Using Maven
1. Ensure you have Docker running (for PostgreSQL via Testcontainers/Docker Compose).
2. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Using Docker Compose
1. Package the application:
   ```bash
   ./mvnw clean package -DskipTests
   ```
2. Run via Docker Compose:
   ```bash
   docker compose up --build
   ```

## Testing
Run the tests with:
```bash
./mvnw clean verify
```
*(Requires a running Docker environment for Testcontainers)*
