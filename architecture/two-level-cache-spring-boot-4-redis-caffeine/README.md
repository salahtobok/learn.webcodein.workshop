# Two-Level L1/L2 Caching with Caffeine and Redis in Spring Boot 4 and Java 25

Companion code repository for the article: [High-Performance Caching in Spring Boot 4: Two-Level L1/L2 Cache with Caffeine and Redis](https://learn.webcodein.com/post/two-level-cache-spring-boot-4-redis-caffeine).

---

## 1. Architectural Overview

This project demonstrates an enterprise **Two-Level (L1/L2) Hybrid Caching Architecture** designed for high-concurrency Spring Boot 4 services running on Java 25 Virtual Threads.

### Key Architectural Concepts:
- **Sub-Microsecond L1 Reads (Caffeine):** Local JVM in-memory cache delivers results in ~50 nanoseconds, bypassing network serialization and socket latency.
- **Distributed Shared L2 (Redis):** Cluster-wide distributed cache maintains state consistency across multiple horizontal application replicas.
- **Cache Stampede Prevention:** Implements single-flight double-checked locking on misses to ensure exactly one thread queries the database during cache warm-up or key expiration.
- **Multi-Node Pub/Sub Invalidation:** Broadcasts mutation events across nodes to guarantee zero stale data in local L1 instances.
- **Automated Architectural Fitness (ArchUnit 1.4.1):** Enforces controller-service boundaries and record immutability.

---

## 2. Technology Stack

- **Java:** JDK 25
- **Framework:** Spring Boot 4.0.0
- **L1 Cache:** Caffeine 3.1.8
- **Testing:** JUnit 5, AssertJ, Spring Boot Test (`MockMvc`), ArchUnit 1.4.1

---

## 3. Running & Verifying Locally

### Maven Verification
To compile the application and execute all unit, slice, and architectural fitness tests:

```bash
mvn clean test
```

### Running the Application
```bash
mvn spring-boot:run
```

---

## 4. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products/{id}` | Fetches product with L1 -> L2 -> DB resolution. |
| `PUT` | `/api/products/{id}` | Updates product in DB and triggers L1 + L2 cache invalidation. |
| `GET` | `/api/products/cache/metrics` | Returns real-time L1 hits, L2 hits, DB loads, and hit rates. |
| `POST` | `/api/products/cache/clear-l1` | Flushes local JVM L1 cache to test L2 Redis fallback. |
