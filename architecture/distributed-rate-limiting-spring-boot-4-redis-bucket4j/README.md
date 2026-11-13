# Distributed Rate Limiting with Spring Boot 4 and Redis Architecture

This companion project demonstrates **Distributed API Rate Limiting and Throttling** using the **Token Bucket Algorithm** with **Java 25** and **Spring Boot 4.0.0**.

## Core Architectural Features
- **Token Bucket Limiting**: Supports burst capacity and steady-state token replenishment.
- **Tier-Based Quotas**: Differentiates between Free, Basic, Premium, and Enterprise API keys.
- **RFC Standard Headers**: Returns `X-RateLimit-Limit`, `X-RateLimit-Remaining`, and `Retry-After` on HTTP 429 responses.
- **ArchUnit 1.4.1 Governance**: Verifies layer isolation and annotation integrity.

## Tech Stack
- Java 25
- Spring Boot 4.0.0
- ArchUnit 1.4.1
- JUnit 5 & MockMvc

## Run Tests
```bash
mvn -B -ntp clean test
```
