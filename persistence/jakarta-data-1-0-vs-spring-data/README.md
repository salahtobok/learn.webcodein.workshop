# Jakarta Data 1.0 vs Spring Data: Modern Enterprise Repository Architecture

Companion workshop project for the technical guide: [Introducing Jakarta Data 1.0: The Powerful Enterprise Alternative to Spring Data](https://learn.webcodein.com/post/introducing-jakarta-data-1-0-the-powerful-enterprise-alternative-to-spring-data).

---

## Architectural Overview

This project demonstrates the core architectural advantages of **Jakarta Data 1.0** (Jakarta EE 11) using **Hibernate ORM 7.1** as the reference implementation on **Java 25**:

1. **Compile-Time Static Code Generation**: Unlike Spring Data JPA which synthesizes dynamic proxies in memory at JVM startup using reflection and CGLIB, Jakarta Data generates concrete repository classes (`EmployeeRepository_`, `ProductRepository_`) during compilation via `hibernate-processor`.
2. **Type-Safe Metamodel Generation**: Generates static metamodel classes (`_Employee`, `_Product`) providing compile-time attribute verification, eliminating runtime SQL typos.
3. **Declarative Query Models**:
   - Parameter-driven queries via `@Find` and `@By("department")`.
   - Custom JPQL/JDQL queries via `@Query` with positional parameters (`?1`).
   - Declarative sorting rules with `@OrderBy("price")` and dynamic sorting with `Order.by(Sort.asc("price"))`.
4. **DTO Projections**: Clean constructor mapping into immutable Java 25 records (`DepartmentSummary`).
5. **Keyset & Offset Pagination**: Paginated windowing using `PageRequest.ofPage(n).size(m)` and `Page<T>` return contracts.
6. **Zero-Reflection Runtimes**: Instant cold starts and 100% native GraalVM AOT compatibility without reachability metadata proxies.

---

## Package Structure

```
com.webcodein.data
├── DepartmentSummary.java           # Immutable Java 25 record DTO projection
├── Employee.java                    # JPA Entity with department, salary, and active status
├── EmployeeRepository.java          # Jakarta Data CrudRepository with @Find, @Query, and DTO projections
├── Product.java                     # JPA Entity with category and pricing
└── ProductRepository.java           # Jakarta Data CrudRepository with PageRequest, @OrderBy, and Order
```

---

## Test Suite Breakdown

The repository contains an exhaustive 13-test suite verifying entity contracts, record immutability, repository lifecycle operations, and annotation-processor-generated implementations:

| Test Class | Scope / Target | Tests | Description |
|---|---|---|---|
| `EmployeeEntityTest` | Unit / Entity | 2 | Verifies entity constructors, accessors, setters, `toString()`, and ID-based equality contracts. |
| `DepartmentSummaryTest` | Unit / Record | 2 | Verifies record component accessors, immutability, and precondition validation contracts. |
| `EmployeeRepositoryTest` | Persistence / H2 | 4 | Verifies CrudRepository `insert`/`findById`, `@Find` with `@By` matching, `@Query` high earners, and JPQL DTO projection into `DepartmentSummary`. |
| `ProductRepositoryTest` | Persistence / H2 | 3 | Verifies `PageRequest` pagination with `Order`, declarative `@OrderBy` sorting, and CRUD mutation lifecycles. |
| `JakartaDataRepositoriesTest` | Integration / H2 | 2 | Verifies multi-repository interactions against Hibernate `StatelessSession`. |

**Total: 13 tests run, 0 failures, 0 errors, 100% success.**

---

## Prerequisites

- **JDK 25** (Oracle OpenJDK or Eclipse Temurin)
- **Maven 3.9+** (or included `./mvnw`)

---

## Building and Testing

Run the full verification suite:

```bash
mvn clean test
```

Or using the Unix-formatted Maven wrapper:

```bash
./mvnw clean test
```

To run complete compilation, annotation processor generation, and packaging:

```bash
mvn clean verify
```

---

## How Compile-Time Generation Works

When you compile with `mvn compile`, `hibernate-processor` automatically analyzes the `@Repository` interfaces and generates the implementation sources under `target/generated-sources/annotations/com/webcodein/data/`:

- `EmployeeRepository_.java`: Direct Java code delegating to Hibernate `StatelessSession` or `Session`.
- `ProductRepository_.java`: Direct Java code managing pagination queries and bounds checking.
- `_Employee.java` & `_Product.java`: Static metamodel interfaces containing `SortableAttribute` and `TextAttribute` singletons for type-safe sorting and querying.

In a containerized or GraalVM native image environment, this eliminates the dynamic class loading and reflection penalties inherent to traditional framework proxies.

---

## Related Articles

- [Hibernate 7.0 & Spring Data JPA 4: The Best New Features](https://learn.webcodein.com/post/hibernate-7-spring-data-jpa-4-new-features)
- [Eliminating the Hibernate 7 & JPA N+1 Query Problem in Spring Boot 4](https://learn.webcodein.com/post/hibernate-7-n-plus-1-query-problem-spring-boot)
- [Optimistic vs. Pessimistic Locking in JPA & Hibernate 7](https://learn.webcodein.com/post/optimistic-pessimistic-locking-spring-boot)
- [Production HikariCP Tuning and Leak Detection in Spring Boot 4](https://learn.webcodein.com/post/hikaricp-connection-pool-tuning-spring-boot)
- [GraalVM Native Image with Spring Boot 4: Cloud-Native Java Guide](https://learn.webcodein.com/post/graalvm-native-image-spring-boot-4-cloud-native-java-guide)
