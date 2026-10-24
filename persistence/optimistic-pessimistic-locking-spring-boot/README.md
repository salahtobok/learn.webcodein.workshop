# Optimistic vs Pessimistic Locking in JPA & Hibernate 7 (Spring Boot 4)

Companion code repository for the tutorial: [Optimistic vs. Pessimistic Locking in JPA & Hibernate 7: Handling Race Conditions in Spring Boot 4](https://learn.webcodein.com/post/optimistic-pessimistic-locking-spring-boot).

## Overview

This project demonstrates practical solutions for high-concurrency race conditions using Spring Boot 4, Hibernate 7, and PostgreSQL:

1. **Lost Update Anomaly**: How uncoordinated concurrent transactions overwrite each other's changes.
2. **Optimistic Locking (`@Version`)**: Detecting concurrent modifications via versioning and resolving conflicts using resilient retry loops.
3. **Pessimistic Locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)**: Acquiring exclusive database row locks (`SELECT ... FOR UPDATE`) to guarantee strict serial updates under high contention.
4. **Non-blocking Queue Allocation (`SKIP LOCKED`)**: Utilizing PostgreSQL `FOR UPDATE SKIP LOCKED` for concurrent worker queue claims without lock contention or thread blocking.

## Tech Stack

- **Java**: 25
- **Framework**: Spring Boot 4.0.0
- **ORM**: Hibernate 7.1.8 / Spring Data JPA 4
- **Database**: PostgreSQL 16
- **Testing**: Testcontainers 2.x, JUnit 5, AssertJ

## Running the Application

### Prerequisites

- JDK 25 installed
- Docker running (for PostgreSQL Testcontainers / Docker Compose)

### Running Integration Tests

Run the full integration test suite:

```bash
mvn clean verify
```

Or using the Maven wrapper:

```bash
./mvnw clean verify
```

### Running Locally with Docker Compose

Start the PostgreSQL database:

```bash
docker compose up -d
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

## API Endpoints

- `POST /api/inventory/products/{id}/decrement-optimistic?quantity=1`: Decrements stock using standard optimistic locking (`@Version`).
- `POST /api/inventory/products/{id}/decrement-optimistic-retry?quantity=1&retries=5`: Decrements stock with automatic retry and exponential backoff.
- `POST /api/inventory/products/{id}/decrement-pessimistic?quantity=1`: Decrements stock using pessimistic write locking (`SELECT ... FOR UPDATE`).
