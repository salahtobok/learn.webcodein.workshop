# Device Flashing Audit Service

This is the companion code for the article [The Architecture of Safely Flashing a Phone: Bootloaders, Fastboot, and Recovery](https://learn.webcodein.com/post/android-flashing-bootloader-fastboot-recovery).

## Overview
This is a Spring Boot 4 / Java 25 microservice that simulates a centralized audit log for devices being flashed (e.g. in a factory or repair center using Fastboot). It uses PostgreSQL via Spring Data JPA and Testcontainers for integration testing.

## Running the Application

### Using Maven
1. Ensure you have Docker running (for PostgreSQL).
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
