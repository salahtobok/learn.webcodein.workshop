# Project Valhalla Performance Benchmark (Workshop)

Companion code for the article: [Mastering Project Valhalla: A Practical Guide to Value Types in Java 25](https://learn.webcodein.com/post/project-valhalla-value-types-java-25).

This project demonstrates the performance and memory benefits of **Project Valhalla Value Types (Value Classes)** in **Java 25** and **Spring Boot 4.0.0**, featuring JMH memory layout microbenchmarks, Spring Data JPA persistence on PostgreSQL 16, and web REST API endpoints.

---

## Architectural Highlights

- **Value Classes vs Identity Objects:** Direct memory layout comparisons between traditional Java reference classes (with 12–16 byte object headers and pointer indirection) versus identity-free value classes.
- **Flattened Arrays & Cache Locality:** Demonstrates how value types are flattened directly into contiguous memory arrays, maximizing CPU L1/L2/L3 cache line utilization (64 bytes) and eliminating GC allocation overhead.
- **Microbenchmarks (JMH):** Includes OpenJDK JMH benchmarks measuring throughput and memory allocation rates.
- **Integration Testing:** Uses Testcontainers with PostgreSQL 16 for automated database testing.

---

## Prerequisites

- **Java 25** (with `--enable-preview`)
- **Maven 3.9+** (or use the included `./mvnw`)
- **Docker** (for running PostgreSQL Testcontainers during tests)

---

## Running the Test Suite

Execute the full suite of unit, MockMvc web slice, ArchUnit 1.4.1 architecture rules, and Testcontainers integration tests:

```bash
./mvnw clean test
```

### Test Suite Structure (11 Tests Passing 100%)
- `com.webcodein.valhalla.PointTest`: Validates coordinates and object properties (2 tests).
- `com.webcodein.valhalla.benchmark.MemoryLayoutBenchmarkTest`: Validates deterministic computation and array initialization (1 test).
- `com.webcodein.valhalla.controller.BenchmarkControllerTest`: `@WebMvcTest` slice testing GET/POST REST APIs (2 tests).
- `com.webcodein.valhalla.ValhallaApplicationTests`: Full Spring Boot 4 integration test running against PostgreSQL 16 Testcontainers (1 test).
- `com.webcodein.valhalla.ValhallaArchitectureTest`: ArchUnit 1.4.1 architectural fitness rules validating controller, repository, and entity boundaries (5 tests).

---

## Running the Application Locally

1. Start PostgreSQL via Docker Compose:
```bash
docker-compose up -d
```

2. Run the Spring Boot application:
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="--enable-preview"
```

3. Query the benchmark REST API:
```bash
curl http://localhost:8080/api/benchmarks
```
