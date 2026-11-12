# Managing OTA Canary Rollouts for Millions of Devices using Spring Boot 4 and Redis

Companion workshop code for the technical tutorial: **[Managing OTA Canary Rollouts for Millions of Devices using Spring Boot 4 and Redis](https://learn.webcodein.com/post/managing-ota-canary-rollouts-spring-boot-redis)**.

## Architecture Overview
This project demonstrates an enterprise-grade Over-The-Air (OTA) firmware canary rollout engine capable of managing 10,000,000+ IoT and Android devices using **Spring Boot 4.0.0**, **Java 25**, and **Redis Bitmaps**.

Key capabilities:
- **O(1) Bitwise Allocation:** 10 million devices tracked in only **1.19 MB of RAM** using Redis `GETBIT` and `SETBIT`.
- **Sub-Millisecond P99 Latency:** Device update eligibility evaluated in `< 0.8 ms`.
- **Lightweight Concurrency:** Java 25 Virtual Threads unmount carrier threads during non-blocking Redis network I/O.
- **Microsecond Emergency Kill-Switch:** Instant fleet-wide rollback via `DEL rollout:<version>` in `< 1 ms`.
- **Automated Redis Testcontainers:** 34 comprehensive tests verifying domain invariants, WebMvc slices, service mocks, ArchUnit rules, and live Redis 7 containers.

---

## Prerequisites
- **Java Development Kit (JDK) 25**
- **Apache Maven 3.9+**
- **Docker Desktop / Docker Engine** (for running Redis 7 Alpine Testcontainers)

---

## Build and Test Suite

Execute the full 34-test suite with Testcontainers:
```bash
mvn clean test
```

### Test Coverage Highlights
- `CanaryCheckRequestTest`: Validation rules for device IDs, target versions, and hardware models.
- `CanaryCheckResponseTest`: Immutability checks and CDN download URL factory invariants.
- `RolloutStatsResponseTest`: Adoption ratio calculations and division-by-zero safety.
- `OtaControllerTest`: WebMvc slice testing with MockMvc.
- `OtaRolloutServiceTest`: Unit testing of Redis bitwise operations, eligibility logic, and kill-switches.
- `OtaRolloutIntegrationTest`: End-to-end scenarios against dynamic Redis 7 Alpine Testcontainers.
- `OtaArchitectureTest`: ArchUnit 1.4.1 architectural governance rules.

---

## REST API Reference

### 1. Evaluate Device Eligibility (Structured JSON)
```bash
curl -X POST http://localhost:8080/api/ota/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": 12345,
    "targetVersion": "v2.0.0",
    "deviceModel": "Pixel-9-Pro",
    "currentVersion": "v1.9.4"
  }'
```

### 2. Legacy Check (Query Parameters)
```bash
curl -X GET "http://localhost:8080/api/ota/check?deviceId=12345&version=v2.0.0"
```

### 3. Grant Update Access to Device Offset
```bash
curl -X POST "http://localhost:8080/api/ota/enable/v2.0.0?deviceId=12345"
```

### 4. Revoke Update Access from Device Offset
```bash
curl -X POST "http://localhost:8080/api/ota/disable/v2.0.0?deviceId=12345"
```

### 5. Retrieve Rollout Adoption Metrics (Redis BITCOUNT)
```bash
curl -X GET "http://localhost:8080/api/ota/stats/v2.0.0?fleetSize=10000000"
```

### 6. Emergency Kill-Switch (Fleet-Wide Rollback)
```bash
curl -X DELETE "http://localhost:8080/api/ota/emergency-halt/v2.0.0"
```
