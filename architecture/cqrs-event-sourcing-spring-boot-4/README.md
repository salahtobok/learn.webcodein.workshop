# CQRS and Event Sourcing with Spring Boot 4 and Java 25

This is the companion code for the article **"CQRS and Event Sourcing with Spring Boot 4 & Java 25"**.

## Prerequisites
- Java 25
- Docker & Docker Compose (for running PostgreSQL and Testcontainers)

## Building the Project
To compile the project and run the integration tests (requires Docker):
```bash
./mvnw clean verify
```

## Running the Application Locally
You can run the application directly using Maven, which will start the required PostgreSQL container automatically using Spring Boot Testcontainers support (if configured for dev-mode) or you can spin up the compose file.

### 1. Start the Database
```bash
docker-compose up -d postgres
```

### 2. Start the App
```bash
./mvnw spring-boot:run
```

## Running with Docker Compose
To run both the application and the database fully containerized:
```bash
docker-compose up --build
```
