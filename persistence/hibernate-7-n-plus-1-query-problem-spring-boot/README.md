# Eliminating the Hibernate 7 & JPA N+1 Query Problem in Spring Boot 4

Companion code repository for the tutorial on [learn.webcodein.com](https://learn.webcodein.com/post/hibernate-7-n-plus-1-query-problem-spring-boot).

This project demonstrates how to identify, reproduce, and eliminate the Hibernate 7 and JPA N+1 query problem using **Java 25**, **Spring Boot 4.0.0**, **Hibernate 7.1.0**, and **PostgreSQL 16**.

---

## What is the N+1 Query Problem?

When querying entities with lazy associations (`@OneToMany` or `@ManyToMany`), fetching $N$ parent entities results in:
1. **1 initial query** to load all parent entities.
2. **$N$ secondary queries** to load the children for each parent as the application traverses the collection.

For 1,000 parent entities, this results in 1,001 database network roundtrips, causing connection pool exhaustion and high latency.

---

## Solutions Demonstrated

| Approach | Description | SQL Queries (5 Parents) | Best Use Case |
| :--- | :--- | :---: | :--- |
| **Unoptimized Lazy** | Classic N+1 anti-pattern | **6** (1 + 5) | Never in loops |
| **JPQL JOIN FETCH** | Single query with INNER/LEFT JOIN | **1** | When full entity graph is needed |
| **JPA @EntityGraph** | Declarative fetch plan | **1** | Dynamic fetching without query rewriting |
| **Hibernate @BatchSize** | Batches lazy fetches into `IN (?, ?, ?)` | **2** (1 + 1) | **Pagination with child collections** |
| **DTO Projection** | Direct column selection into Record | **1** | Read-heavy queries & REST endpoints |

---

## Project Structure

```
hibernate-7-n-plus-1-query-problem-spring-boot/
├── src/
│   ├── main/java/com/webcodein/nplusone/
│   │   ├── NPlusOneApplication.java
│   │   ├── controller/AuthorController.java
│   │   ├── domain/
│   │   │   ├── Author.java
│   │   │   ├── Book.java
│   │   │   └── Publisher.java
│   │   ├── dto/AuthorSummaryDto.java
│   │   ├── inspector/SqlStatementCountInspector.java
│   │   ├── repository/
│   │   │   ├── AuthorRepository.java
│   │   │   ├── BookRepository.java
│   │   │   └── PublisherRepository.java
│   │   └── service/AuthorBookService.java
│   └── test/java/com/webcodein/nplusone/
│       └── NPlusOneDetectionAndFixesIT.java
├── pom.xml
├── Dockerfile
└── docker-compose.yml
```

---

## Running Integration Tests

The test suite runs against a live **PostgreSQL 16** Testcontainer and asserts exact query counts using Hibernate 7's `StatementInspector`:

```bash
mvn -B -ntp clean verify
```

Expected output:
```
[INFO] Running com.webcodein.nplusone.NPlusOneDetectionAndFixesIT
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Running Locally

1. Start PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Start the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Test endpoints:
   - Unoptimized (triggers N+1): `http://localhost:8080/api/authors/unoptimized`
   - Fixed with JOIN FETCH: `http://localhost:8080/api/authors/join-fetch`
   - Fixed with @EntityGraph: `http://localhost:8080/api/authors/entity-graph`
   - Fixed with @BatchSize: `http://localhost:8080/api/publishers/batch-fetch`
   - Fixed with DTO Projection: `http://localhost:8080/api/authors/summaries`
