# Hibernate 7.0 & Spring Data JPA 4: The Best New Features

Companion workshop project for the technical guide: [Hibernate 7.0 & Spring Data JPA 4: The Best New Features](https://learn.webcodein.com/post/hibernate-7-spring-data-jpa-4-new-features).

## Architectural Overview

This project showcases the major enterprise persistence features introduced in **Hibernate 7.1** and **Spring Data JPA 4** running on **Spring Boot 4.0.0** and **Java 25**:

1. **Native JSONB Mapping & Querying**: Out-of-the-box JSONB persistence via `@JdbcTypeCode(SqlTypes.JSON)` paired with deep JSON querying using HQL `json_value(p.metadata, '$.manufacturer')`.
2. **SQL Window Functions in JPQL**: Native analytical queries using `RANK()`, `DENSE_RANK()`, and `ROW_NUMBER()` with `OVER (PARTITION BY ... ORDER BY ...)` directly within JPA repositories.
3. **Smart Collection Fetching**: Preventing `MultipleBagFetchException` and Cartesian explosion by leveraging `Set` semantics with `@EntityGraph`.
4. **Zero-Reflection Java Record Projections**: Compiling lightweight, reflection-free DTO queries directly into Java 25 records with zero dirty-checking or first-level cache overhead.
5. **REST API Layer**: Exposing Spring MVC endpoints for querying products, ranked employees, active users, and status-filtered orders.

---

## Package Structure

```
com.webcodein.shop
├── ShopApplication.java               # Spring Boot 4 entry point
├── employee/
│   ├── Employee.java                  # Entity with department, salary, and window functions
│   └── EmployeeRepository.java        # Repository with JPQL OVER(PARTITION BY ...)
├── order/
│   ├── Discount.java                  # Discount child entity in Order aggregate
│   ├── LineItem.java                  # LineItem child entity in Order aggregate
│   ├── Order.java                     # Root entity with Set-based collections
│   ├── OrderRepository.java           # @EntityGraph multi-collection fetch query
│   └── OrderStatus.java               # Order status enumeration (PAID, SHIPPED, etc.)
├── product/
│   ├── Product.java                   # Entity with @JdbcTypeCode(SqlTypes.JSON) column
│   ├── ProductMetadata.java           # Java 25 Record mapped to JSONB
│   └── ProductRepository.java         # HQL json_value() querying
├── user/
│   ├── AppUser.java                   # User entity (mapped to table app_user)
│   ├── UserRepository.java            # DTO projection repository
│   └── UserSummary.java               # Java 25 Record DTO projection
└── web/
    └── ShopController.java            # REST controller exposing endpoints
```

---

## Test Suite Breakdown

The repository contains an exhaustive 11-test suite combining unit tests, web slice tests, and containerized PostgreSQL integration tests:

| Test Class | Scope / Target | Tests | Description |
|---|---|---|---|
| `ProductMetadataTest` | Unit / Record | 2 | Verifies Java 25 record contract, immutability, equals/hashCode, and Jackson serialization roundtrip. |
| `UserSummaryTest` | Unit / Record | 1 | Verifies Record projection accessors, immutability, and equality invariants. |
| `ShopControllerTest` | Web Slice (`@WebMvcTest`) | 4 | Verifies GET endpoints for product search, employee department ranking, active users, and orders. |
| `ShopRepositoriesIntegrationTest` | Testcontainers (`PostgreSQL 16`) | 4 | Verifies live JSONB `json_value()` queries, JPQL window function partitions, `@EntityGraph` dual-set joins, and record projections against PostgreSQL 16. |

---

## Prerequisites

- **JDK 25** (Oracle OpenJDK or Eclipse Temurin)
- **Maven 3.9+** (or included `./mvnw`)
- **Docker Desktop** (for Testcontainers PostgreSQL 16)

---

## Building and Testing

Run the full verification suite against the containerized PostgreSQL database:

```bash
mvn clean verify
```

Or using the Unix-formatted Maven wrapper:

```bash
./mvnw clean verify
```

---

## Running the Application Locally

Start the application with standard Maven execution:

```bash
mvn spring-boot:run
```

Ensure a PostgreSQL instance is running on `localhost:5432` with database `test` (or adjust `application.yml`).

### Example REST API Requests

```bash
# Search products by JSON metadata manufacturer
curl -s "http://localhost:8080/api/products/search?manufacturer=Acme"

# Retrieve employees with analytical department rank
curl -s "http://localhost:8080/api/employees/ranks"

# Retrieve lightweight active user projections (Java Records)
curl -s "http://localhost:8080/api/users/active"

# Retrieve orders with eagerly fetched LineItems and Discounts
curl -s "http://localhost:8080/api/orders/PAID"
```

---

## Differences from the Blog Post

The blog post uses concise conceptual snippets. The companion repository expands them with enterprise hardening:
1. **Reserved Words**: The `User` entity is mapped to table `app_user` (`AppUser.java`) because `user` is a reserved SQL keyword in PostgreSQL.
2. **HQL JSON Syntax**: In Hibernate 7.1, querying inside a JSON document is performed via `json_value(p.metadata, '$.path')`. This feature is enabled via `hibernate.query.hql.json_functions_enabled=true` in `application.yml` and requires Jackson 2 on the classpath.
3. **Collection Semantics**: To prevent `MultipleBagFetchException` and Cartesian multiplication, multi-collection entity graphs map associations as `java.util.Set` rather than unordered bags (`List`).

---

## Related Articles

- [Hibernate 7.0 & Spring Data JPA 4: The Best New Features](https://learn.webcodein.com/post/hibernate-7-spring-data-jpa-4-new-features)
- [Eliminating the Hibernate 7 & JPA N+1 Query Problem in Spring Boot 4](https://learn.webcodein.com/post/hibernate-7-n-plus-1-query-problem-spring-boot)
- [Optimistic vs. Pessimistic Locking in JPA & Hibernate 7](https://learn.webcodein.com/post/optimistic-pessimistic-locking-spring-boot)
- [Production HikariCP Tuning and Leak Detection in Spring Boot 4](https://learn.webcodein.com/post/hikaricp-connection-pool-tuning-spring-boot)
