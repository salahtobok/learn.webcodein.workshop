# Production HikariCP Connection Pool Sizing and Leak Detection in Spring Boot 4

This project demonstrates how to configure and tune **HikariCP** for production workloads using Spring Boot 4, Java 25, and PostgreSQL.

Companion code for the tutorial: [Production HikariCP Tuning and Leak Detection in Spring Boot 4: Step-by-Step Tutorial](https://learn.webcodein.com/post/hikaricp-connection-pool-tuning-spring-boot).

---

## Key Architectural Concepts

1. **Mathematical Pool Sizing**:
   Uses the PostgreSQL/HikariCP recommended sizing formula:
   $$\text{connections} = ((\text{core\_count} \times 2) + \text{effective\_spindle\_count})$$
   Avoids context-switching overhead and lock thrashing caused by arbitrarily large pools.

2. **Fixed-Size Pool**:
   Setting `minimum-idle` equal to `maximum-pool-size` (10 connections) eliminates latency spikes caused by dynamically spinning up TCP connections during traffic bursts.

3. **Fail-Fast Connection Timeout**:
   Setting `connection-timeout: 3000ms` (3 seconds) ensures client requests fail fast rather than hanging for the default 30 seconds and exhausting Tomcat's thread pool.

4. **Watchdog Leak Detection**:
   Configuring `leak-detection-threshold: 2000ms` captures an allocating stack trace whenever a database connection remains checked out longer than 2 seconds.

5. **Runtime Metrics Exposure**:
   Inspects `HikariPoolMXBean` at `/api/pool/metrics` to expose active, idle, total, and threads awaiting connections in real time.

---

## Prerequisites

- **Java 25** (JDK 25)
- **Maven 3.9+** (or included `./mvnw`)
- **Docker & Docker Compose** (for running PostgreSQL and containerized integration tests)

---

## Running the Application Locally

### 1. Start the PostgreSQL Container
```bash
docker compose up postgres -d
```

### 2. Build and Run via Maven
```bash
./mvnw clean spring-boot:run
```

The application starts on `http://localhost:8080`.

---

## Testing the Endpoints

### 1. Create an Order (Fast Transaction)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Alice Walker","productCode":"TECH-400","amount":199.99}'
```

### 2. Check Pool Metrics
```bash
curl http://localhost:8080/api/pool/metrics
```
Response:
```json
{
  "poolName": "ProductionHikariPool",
  "activeConnections": 0,
  "idleConnections": 10,
  "totalConnections": 10,
  "threadsAwaitingConnection": 0,
  "maximumPoolSize": 10,
  "minimumIdle": 10
}
```

### 3. Simulate Connection Leak (Watchdog Trigger)
```bash
curl http://localhost:8080/api/orders/leak
```
After 2 seconds, check application console logs. HikariCP will output:
```
WARN  c.z.h.p.ProxyLeakTask - Apparent connection leak detected on com.zaxxer.hikari.pool.ProxyConnection...
```

Release leaked connection:
```bash
curl -X POST http://localhost:8080/api/orders/leak/release
```

---

## Running Verification Tests
Execute the integration tests (powered by Testcontainers):
```bash
./mvnw clean verify
```
