# OWASP API Security Top 10: Architectural Mitigations in Java 25

Companion workshop code for the technical article:
**"OWASP API Security Top 10: Deep Architectural Mitigation Guide for Modern Backends"** published on [learn.webcodein.com](https://learn.webcodein.com/post/owasp-api-security-top-10-architectural-mitigations).

## Overview
This workshop project provides pure Java 25 architectural mitigation implementations for critical API security vulnerabilities:
- **API1:2023 Broken Object Level Authorization (BOLA):** Context-aware access boundary verifying ownership and tenant residency prior to business logic execution.
- **API3:2023 Broken Object Property Level Authorization (BOPLA / Mass Assignment):** Schema inspection engine and strict DTO binding preventing injection of internal sensitive fields.
- **ArchUnit 1.4.1 Architectural Governance:** Enforces strict domain layering, record immutability, and boundary protection.

## Prerequisites
- Java 25 SDK (Corretto 25 or Temurin 25)
- Maven 3.9+ or included `./mvnw`

## Running Tests
Run all unit and architecture tests:
```bash
./mvnw clean test
```

## Running the Security Demonstration
```bash
./mvnw compile exec:java -Dexec.mainClass="com.webcodein.workshop.owasp.OwaspMitigationsApplication"
```

## Running with Docker
```bash
docker compose up --build
```
