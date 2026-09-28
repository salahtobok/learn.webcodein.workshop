# JobRunr Distributed Background Jobs with Spring Boot 4

This is the companion code for the Webcodein article: **Ultimate Guide to Distributed Background Jobs in Java 25 with JobRunr & Spring Boot 4**.

This project demonstrates how to set up and configure JobRunr in a Spring Boot 4 application, leveraging PostgreSQL for the persistent job store and Java 25 Virtual Threads for high-throughput background processing.

## Prerequisites
- Java 25
- Maven 3.9+
- Docker & Docker Compose (for PostgreSQL and Testcontainers)

## Running the Application Locally (Maven + Docker Compose)

1. **Start PostgreSQL**:
   ```bash
   docker-compose up -d postgres
   ```
2. **Build and Run**:
   ```bash
   ./mvnw spring-boot:run
   ```
3. **Access JobRunr Dashboard**:
   Open `http://localhost:8000` in your browser.

4. **Trigger Jobs**:
   - Enqueue a fire-and-forget job:
     ```bash
     curl -X POST "http://localhost:8080/api/jobs/enqueue?email=dev@example.com"
     ```
   - Schedule a delayed job:
     ```bash
     curl -X POST "http://localhost:8080/api/jobs/schedule"
     ```

## Running the Application (Fully Dockerized)

```bash
docker-compose up --build
```
This will start both PostgreSQL and the Spring Boot application.

## Testing

This project uses Testcontainers to spin up an ephemeral PostgreSQL instance for integration testing.

```bash
./mvnw clean verify
```
