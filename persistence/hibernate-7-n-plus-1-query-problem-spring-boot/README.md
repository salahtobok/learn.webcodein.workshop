# Eliminating the Hibernate 7 & JPA N+1 Query Problem in Spring Boot 4

Companion code repository for the production engineering guide on diagnosing, reproducing, and eliminating the Hibernate 7 and JPA N+1 query problem in Spring Boot 4 and Java 25.

---

## What is the N+1 Query Problem?

When an application queries entities with lazy collections (`@OneToMany` or `@ManyToMany`), accessing child associations inside a loop triggers:
1. **1 initial SQL query** to retrieve $N$ parent records.
2. **$N$ secondary SQL queries** to retrieve the related children for each parent individually.

Under high concurrency, 1,000 parent entities result in 1,001 database roundtrips. This saturates the HikariCP connection pool, spikes database CPU usage, and introduces severe network latency.

```
                    Client HTTP Request
                             |
                             v
               +---------------------------+
               |  Spring Boot 4 Service    |
               +---------------------------+
                             |
                   1. SELECT * FROM authors (Returns N rows)
                             |
                             v
              +-----------------------------+
              |      PostgreSQL Database    |
              +-----------------------------+
               |    |    |          |     |
               |    |    |          |     +--> N. SELECT * FROM books WHERE author_id = N
               |    |    |          +--------> 3. SELECT * FROM books WHERE author_id = 3
               |    |    +-------------------> 2. SELECT * FROM books WHERE author_id = 2
               |    +------------------------> 1. SELECT * FROM books WHERE author_id = 1
               +-----------------------------> Network Round-Trip Overhead Spikes!
```

---

## 4 Production Fix Strategies Demonstrated

| Strategy | Mechanism | Query Count (5 Parents) | Best Use Case |
|---|---|:---:|---|
| **Unoptimized Lazy** | Classic N+1 bug | **6 queries** (1 + 5) | Anti-pattern; avoid in loops |
| **JPQL `JOIN FETCH`** | Single `INNER/LEFT JOIN` in query | **1 query** | When complete entity graph is needed for updates |
| **JPA `@EntityGraph`** | Declarative fetch plan | **1 query** | Dynamic fetching across multiple repository methods |
| **Hibernate `@BatchSize`** | Batches lazy fetches into `WHERE IN (?, ?)` | **2 queries** (1 + 1) | **Paginated queries with child collections** |
| **Java 25 DTO Projection** | Direct column selection into `record` | **1 query** | Read-heavy REST endpoints (zero entity overhead) |

---

## Project Structure

```
hibernate-7-n-plus-1-query-problem-spring-boot/
├── pom.xml                                    # Java 25, Spring Boot 4.0.0, Hibernate 7.1, Testcontainers
├── src/
│   ├── main/
│   │   ├── java/com/webcodein/nplusone/
│   │   │   ├── NPlusOneApplication.java       # Spring Boot application runner
│   │   │   ├── controller/
│   │   │   │   └── AuthorController.java      # REST endpoints demonstrating each strategy
│   │   │   ├── domain/
│   │   │   │   ├── Author.java                # Parent entity with lazy books collection
│   │   │   │   ├── Book.java                  # Child entity with author & publisher
│   │   │   │   └── Publisher.java             # Entity with @BatchSize(size = 30) collection
│   │   │   ├── dto/
│   │   │   │   └── AuthorSummaryDto.java      # Immutable Java 25 record DTO projection
│   │   │   ├── inspector/
│   │   │   │   └── SqlStatementCountInspector.java # Hibernate 7 StatementInspector query counter
│   │   │   ├── repository/
│   │   │   │   ├── AuthorRepository.java      # JOIN FETCH, @EntityGraph, DTO projection queries
│   │   │   │   ├── BookRepository.java        # Standard JPA book repository
│   │   │   │   └── PublisherRepository.java   # Publisher repository with batch fetching
│   │   │   └── service/
│   │   │       └── AuthorBookService.java     # Business service executing each scenario
│   │   └── resources/
│   │       └── application.yml                # Hibernate 7 dialect, inspector, and Hikari pool
│   └── test/
│       └── java/com/webcodein/nplusone/
│           ├── HibernateArchitectureTest.java # 7 ArchUnit layer and entity governance rules
│           ├── NPlusOneDetectionAndFixesTest.java # 5 PostgreSQL 16 Testcontainers integration scenarios
│           ├── controller/
│           │   └── AuthorControllerTest.java  # WebMvc slice tests with MockMvc
│           ├── domain/
│           │   ├── AuthorTest.java            # Author entity lifecycle unit tests
│           │   ├── BookTest.java              # Book entity association unit tests
│           │   └── PublisherTest.java         # Publisher entity unit tests
│           └── dto/
│               └── AuthorSummaryDtoTest.java  # DTO projection record unit tests
└── README.md
```

---

## Verification Test Suite (19 / 19 Tests Passed)

Execute the full verification test suite using Apache Maven:

```bash
mvn clean test
```

### Test Breakdown

1. **`NPlusOneDetectionAndFixesTest`** (5 integration tests on PostgreSQL 16 Testcontainers):
   - `reproduceNPlusOneQueryProblem`: Asserts exactly 6 SQL queries (1 initial + 5 lazy fetches) executed when reading authors unoptimized.
   - `fixWithJoinFetch`: Asserts exactly 1 SQL query executed when using `JOIN FETCH`.
   - `fixWithEntityGraph`: Asserts exactly 1 SQL query executed when using `@EntityGraph`.
   - `fixWithBatchFetching`: Asserts exactly 2 SQL queries executed (1 publisher query + 1 batched IN-clause query) when using `@BatchSize(size = 30)`.
   - `fixWithDtoProjection`: Asserts exactly 1 SQL query executed selecting scalar columns directly into `AuthorSummaryDto` record.
2. **`AuthorControllerTest`** (2 WebMvc slice tests):
   - `shouldReturnAuthorSummaries`: Tests `GET /api/authors/summaries` returning JSON projections.
   - `shouldReturnAuthorsWithJoinFetch`: Tests `GET /api/authors/join-fetch`.
3. **`Domain Unit Tests`** (5 tests):
   - `AuthorTest` (2 tests), `BookTest` (1 test), `PublisherTest` (1 test), `AuthorSummaryDtoTest` (1 test).
4. **`HibernateArchitectureTest`** (7 ArchUnit rules):
   - Enforces layer isolation, repository interfaces, `@Entity` package location, `@RestController` stereotypes, and forbids generic exceptions.

---

## Tech Stack
- **Java**: 25 (OpenJDK)
- **Framework**: Spring Boot 4.0.0 GA
- **Persistence**: Hibernate 7.1.0, Spring Data JPA 4
- **Database**: PostgreSQL 16 Alpine
- **Testing**: Testcontainers 2.0.2, JUnit 5, AssertJ, Spring Test MockMvc, ArchUnit 1.4.1
