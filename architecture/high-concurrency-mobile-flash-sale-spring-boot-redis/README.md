# High-Concurrency Mobile Flash Sale Backend with Spring Boot 4 and Redis

This is the companion code for the tutorial: [Building a High-Concurrency Mobile Flash Sale Backend with Spring Boot 4 and Redis](https://learn.webcodein.com/post/high-concurrency-mobile-flash-sale-spring-boot-redis).

## Overview
This workshop demonstrates how to build a highly concurrent backend capable of handling a "flash sale" without overselling inventory. We use Spring Boot 4, Testcontainers, and a Redis Lua script for atomic inventory decrements.

## Prerequisites
- Java 25
- Docker (for Testcontainers and docker-compose)

## Running the Application Locally
1. Build the project (tests will spin up Postgres and Redis via Testcontainers):
   ```bash
   ./mvnw clean verify
   ```
2. Start the application with Docker Compose:
   ```bash
   docker compose up --build
   ```

## Testing the Flash Sale Endpoint
You can manually test the endpoint using curl:
```bash
curl -X POST "http://localhost:8080/api/flash-sale/IPHONE15/purchase?userId=user123"
```
