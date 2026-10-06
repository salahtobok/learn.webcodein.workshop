# Managing OTA Canary Rollouts for Millions of Devices using Spring Boot 4 and Redis

This is the companion code for the Webcodein article on managing OTA (Over-The-Air) canary rollouts.

## Prerequisites
- Java 25
- Docker (for Testcontainers and Redis)

## Building the Project
Run the Maven wrapper to build the project and run tests:
```bash
./mvnw clean verify
```

## Running Locally via Maven
First, ensure you have a local Redis instance running. You can start one using Docker:
```bash
docker run -p 6379:6379 -d redis:7.2-alpine
```

Then run the application:
```bash
./mvnw spring-boot:run
```

## Running via Docker Compose
To run both the application and Redis together:
```bash
docker-compose up --build
```
