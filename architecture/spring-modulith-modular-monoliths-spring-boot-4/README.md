# Spring Modulith: Modular Monoliths in Spring Boot 4

This is the companion project for the article **Building Modular Monoliths with Spring Modulith and Spring Boot 4**.

## Overview
This project demonstrates how to use Spring Modulith to enforce module boundaries and use event-driven communication (via `@ApplicationModuleListener`) instead of tight coupling between packages.

It contains two modules:
- `order`
- `inventory`

## How to run locally

1. Ensure you have Java 25 installed.
2. Build the project:
   ```bash
   ./mvnw clean package
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Docker
A Dockerfile is provided to containerize the application:
```bash
docker build -t spring-modulith-demo .
docker run -p 8080:8080 spring-modulith-demo
```
