# Master ArchUnit: Custom Rules and Conditions

This is the companion code for the article **Master ArchUnit: Writing Custom Rules and Conditions for Spring Boot 4** on Webcodein.

## Overview
This project demonstrates how to use ArchUnit to write advanced, custom architectural fitness functions for a Spring Boot 4 application.

It includes examples of:
1. Custom annotations (`@UseCase`) and preventing web layer dependencies.
2. Enforcing package-private scope on repositories.
3. Preventing layer leakage (DTOs in Domain).
4. Custom `ArchCondition` to prevent the use of legacy classes like `java.util.Date`.

## Running locally

### Prerequisites
- Java 25
- Maven 3.9+

### Build and Test
To build the project and run the ArchUnit tests:

```bash
./mvnw clean verify
```

### Run the application
```bash
./mvnw spring-boot:run
```

### Run via Docker
```bash
docker build -t archunit-custom-rules .
docker run -p 8080:8080 archunit-custom-rules
```
