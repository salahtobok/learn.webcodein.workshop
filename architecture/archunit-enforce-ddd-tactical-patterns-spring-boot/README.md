# Enforcing DDD Tactical Patterns with ArchUnit in Spring Boot 4

This is the companion code for the Webcodein article: [Enforcing DDD Tactical Patterns with ArchUnit in Spring Boot 4](https://learn.webcodein.com/post/archunit-enforce-ddd-tactical-patterns-spring-boot).

This project demonstrates how to use **ArchUnit** to enforce Domain-Driven Design (DDD) tactical patterns in a Java 25 / Spring Boot 4 application.

## Prerequisites
- Java 25
- Maven 3.9+
- Docker (optional)

## Running Guide

### 1. Build and Run Tests
Run the following command to execute all tests (including ArchUnit tests):
```bash
./mvnw clean verify
```

### 2. Run Locally via Maven
```bash
./mvnw spring-boot:run
```

### 3. Run via Docker
Build the docker image:
```bash
docker build -t webcodein/archunit-ddd .
```
Run the container:
```bash
docker run -p 8080:8080 webcodein/archunit-ddd
```
