# First DDD Application with Spring Boot 4 and Java 25

Companion workshop code for the tutorial: [Build Your First DDD Application with Spring Boot 4: Step-by-Step Tutorial](https://learn.webcodein.com/post/first-ddd-application-spring-boot-4-tutorial).

## Overview

This project demonstrates practical Domain-Driven Design (DDD) tactical patterns in a modern Java 25 and Spring Boot 4 application:

- **Domain Model**:
  - **Aggregate Root**: `Project` encapsulates internal collections, enforces domain invariants (maximum 50 tasks, no duplicate titles, completion prerequisites), and registers domain events.
  - **Entity**: `Task` manages its own lifecycle transitions (`TODO` -> `IN_PROGRESS` -> `DONE` or `CANCELLED`).
  - **Value Object**: `Priority` (Java record with bounds checking and preset levels `LOW`, `MEDIUM`, `HIGH`, `URGENT`).
  - **Domain Events**: `TaskAddedEvent`, `ProjectCompletedEvent`.
  - **Domain Service**: `TaskAssignmentService` orchestrates domain task assignment policies.
  - **Domain Repository Interface**: `ProjectRepository` defines persistence contracts without framework coupling.
- **Application Layer**:
  - `ProjectApplicationService` coordinates transactions, use cases, and DTO mappings (`CreateProjectCommand`, `AddTaskCommand`, `ProjectDto`, `TaskDto`).
- **Infrastructure Layer**:
  - `JpaProjectRepository` implements `ProjectRepository` delegating to `SpringDataProjectRepository`.
  - Pure Spring configuration bean wiring for domain services (`DomainConfiguration`).
- **Presentation Layer**:
  - `ProjectController` exposes REST API endpoints with standardized HTTP status codes (201 Created, 200 OK, 204 No Content, 400 Bad Request, 409 Conflict, 404 Not Found).
  - `GlobalExceptionHandler` handles domain exceptions gracefully.

## Prerequisites

- Java 25 JDK
- Maven 3.9+
- Docker (optional, for running with PostgreSQL via `docker-compose`)

## Building and Testing

Run unit, integration, and architectural tests:

```bash
mvn clean test
```

Build the executable Spring Boot artifact:

```bash
mvn clean verify
```

### Architectural Verification (ArchUnit)

The project includes strict architectural rules in `DddArchitectureTest`:
- Verifies domain purity: domain classes never depend on presentation, application, or infrastructure layers.
- Enforces layered architecture boundaries across Domain, Application, Infrastructure, and Presentation.

## Running the Application

### 1. In-Memory Mode (Default Test Profile)

Runs with in-memory H2 database:

```bash
mvn spring-boot:run
```

### 2. Docker Compose (PostgreSQL)

Start PostgreSQL database:

```bash
docker compose up -d db
```

Run application connected to PostgreSQL:

```bash
mvn spring-boot:run
```

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/projects` | Create a new project aggregate |
| `GET` | `/api/projects` | List all projects |
| `GET` | `/api/projects/{id}` | Get project aggregate details and tasks |
| `POST` | `/api/projects/{id}/tasks` | Add a task to project aggregate |
| `PUT` | `/api/projects/{id}/tasks/{taskId}/start` | Transition task to `IN_PROGRESS` |
| `PUT` | `/api/projects/{id}/tasks/{taskId}/complete` | Transition task to `DONE` |
| `PUT` | `/api/projects/{id}/complete` | Complete project aggregate |
| `DELETE` | `/api/projects/{id}` | Delete project aggregate |
