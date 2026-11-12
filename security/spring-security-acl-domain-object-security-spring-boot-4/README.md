# Spring Security ACL: Domain Object Security in Spring Boot 4

Companion code repository for the tutorial: [Mastering Spring Security ACL: Domain Object Security in Spring Boot 4](https://learn.webcodein.com/post/spring-security-acl-domain-object-security-spring-boot-4).

This workshop project demonstrates how to implement fine-grained Domain Object Security (instance-level Access Control Lists) in Spring Boot 4 using Spring Security ACL, Caffeine caching, and PostgreSQL 16 on Java 25.

## Overview & Architecture

Standard Role-Based Access Control (RBAC) grants or denies access based on user roles (e.g., `ROLE_USER`, `ROLE_ADMIN`). However, RBAC cannot evaluate instance-level permissions (e.g., "User Alice can edit Document #42, but User Bob can only read Document #42").

This project implements fine-grained instance security using the Spring Security ACL subsystem:

1. **Relational ACL Schema (`schema.sql`)**: Manages security identities (`acl_sid`), domain classes (`acl_class`), object identities (`acl_object_identity`), and permission bitmasks (`acl_entry`).
2. **Method Security Pre/Post Authorization (`DocumentService.java`)**: Enforces annotations such as `@PreAuthorize("hasPermission(#id, 'com.webcodein.security.acl.entity.Document', 'READ')")` and `@PreAuthorize("hasPermission(#id, 'com.webcodein.security.acl.entity.Document', 'WRITE')")`.
3. **High-Performance L2 Cache (`AclConfig.java`)**: Uses Caffeine Cache via `SpringCacheBasedAclCache` to eliminate repetitive database queries for hot ACL records.
4. **Presentation Layer (`DocumentController.java`)**: REST endpoints delegating domain operations to secured services.
5. **Architectural Governance (ArchUnit 1.4.1)**: Verifies package isolation, REST controller annotations, repository interfaces, and prevents direct repository access from controllers.

## Tech Stack

- **Java**: 25
- **Framework**: Spring Boot 4.0.0
- **Security**: Spring Security 7, Spring Security ACL
- **Cache**: Caffeine L2 Cache
- **Database**: PostgreSQL 16
- **Testing**: Testcontainers PostgreSQL, MockMvc, JUnit 5, AssertJ, ArchUnit 1.4.1

## Prerequisites

- JDK 25 installed
- Docker running (for PostgreSQL Testcontainers or Docker Compose)
- Maven 3.9+ (or use the included `./mvnw`)

## Running the Application Locally

Run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or using Docker Compose:

```bash
docker compose up --build
```

## Running the Test Suite

Run the full test suite (including Testcontainers PostgreSQL ACL integration, controller slices, domain unit tests, and ArchUnit rules):

```bash
mvn clean test
```

## Test Structure

- `AclDemoApplicationTests`: Testcontainers PostgreSQL 16 integration verifying multi-user ACL isolation, read/write permissions, and controller security.
- `DocumentControllerTest`: MockMvc slice testing REST endpoints and security principal propagation.
- `DocumentTest`: Domain unit tests verifying entity construction and property mutations.
- `AclArchitectureTest`: ArchUnit 1.4.1 rules enforcing entity placement, REST controller annotations, service declarations, interface-driven repositories, and controller layer isolation.
