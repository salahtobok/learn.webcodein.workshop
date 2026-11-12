# High-Concurrency Mobile Flash Sale Backend with Spring Boot 4 and Redis

Companion workshop code for the technical guide: **Building a High-Concurrency Mobile Flash Sale Backend with Spring Boot 4 and Redis**.

This project implements an enterprise-grade flash sale backend capable of absorbing massive traffic spikes without overselling inventory. It leverages Spring Boot 4.0.0, Java 25, Redis Lua scripting for atomic inventory decrements, and asynchronous order persistence in PostgreSQL.

---

## Architectural Highlights

- **Atomic Redis Lua Scripting:** Executes inventory verification and decrements inside a single atomic Redis engine thread, preventing race conditions and double-spending without distributed lock overhead.
- **Asynchronous Storage Decoupling:** Immediately confirms stock to the mobile client while delegating heavy relational database persistence to background threads via `@Async`.
- **Testcontainers Dynamic Connection:** Automated integration testing using `@ServiceConnection` with PostgreSQL 16 Alpine and Redis 7 Alpine containers.
- **Architectural Governance:** ArchUnit 1.4.1 test suite verifying package containment, controller annotations, and modern `java.time` usage.

---

## Technical Stack

- **Java:** OpenJDK 25
- **Framework:** Spring Boot 4.0.0 GA
- **Cache & Concurrency:** Spring Data Redis, Redis 7 (Lua Engine)
- **Database:** PostgreSQL 16 Alpine, Spring Data JPA, Hibernate 7
- **Integration Testing:** Testcontainers 2.0.2 (`testcontainers-postgresql`, `testcontainers-junit-jupiter`)
- **Architecture Governance:** ArchUnit JUnit 5 1.4.1

---

## Project Structure

```
high-concurrency-mobile-flash-sale-spring-boot-redis/
??? pom.xml
??? docker-compose.yml
??? src/
    ??? main/
    ?   ??? java/com/webcodein/architecture/flashsale/
    ?   ?   ??? FlashSaleApplication.java          # Spring Boot main entry point (@EnableAsync)
    ?   ?   ??? config/
    ?   ?   ?   ??? RedisConfig.java               # Configures RedisTemplate and pre-loads Lua scripts
    ?   ?   ??? controller/
    ?   ?   ?   ??? FlashSaleController.java       # REST endpoint: POST /api/flash-sale/{productId}/purchase
    ?   ?   ??? domain/
    ?   ?   ?   ??? Order.java                     # JPA Entity representing confirmed purchase
    ?   ?   ?   ??? OrderRepository.java           # Spring Data JPA repository
    ?   ?   ??? service/
    ?   ?       ??? FlashSaleService.java          # Redis Lua atomic decrement coordination
    ?   ?       ??? OrderProcessor.java            # Asynchronous PostgreSQL order writer
    ?   ??? resources/
    ?       ??? scripts/
    ?           ??? decrement_inventory.lua        # Atomic Lua script for inventory checks
    ??? test/
        ??? java/com/webcodein/architecture/flashsale/
            ??? FlashSaleArchitectureTest.java     # 5 ArchUnit architecture and package governance rules
            ??? FlashSaleIntegrationTest.java      # 100-thread concurrent overselling stress test
            ??? TestFlashSaleApplication.java      # Testcontainers @ServiceConnection launcher
            ??? controller/
            ?   ??? FlashSaleControllerTest.java   # MockMvc slice tests
            ??? domain/
            ?   ??? OrderTest.java                 # Entity unit tests
            ??? service/
                ??? FlashSaleServiceTest.java      # Service unit tests with Mockito
```

---

## Building and Testing

### Prerequisites
- JDK 25 installed and available on `PATH`
- Docker Desktop or a running Docker daemon (for Testcontainers)

### Run Test Suite
To run all unit tests, ArchUnit governance rules, and the multi-threaded Testcontainers integration test:

```bash
mvn clean test
```

### Build Runnable Jar
```bash
mvn clean package -DskipTests
```

---

## API Usage Examples

### 1. Purchase Product (Stock Available)

```bash
curl -X POST "http://localhost:8080/api/flash-sale/IPHONE15/purchase?userId=user123"
```

**Response (HTTP 200 OK):**
```
Purchase successful! Order is being processed.
```

### 2. Purchase Product (Sold Out)

```bash
curl -i -X POST "http://localhost:8080/api/flash-sale/IPHONE15/purchase?userId=user999"
```

**Response (HTTP 400 Bad Request):**
```
HTTP/1.1 400 Bad Request
Sold out!
```
