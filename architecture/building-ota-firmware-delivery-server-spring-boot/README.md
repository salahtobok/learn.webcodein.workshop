# Building an OTA Firmware Delivery Server with Spring Boot 4 and Java 25

[![Build Status](https://github.com/salahtobok/learn.webcodein.workshop/actions/workflows/build.yml/badge.svg)](https://github.com/salahtobok/learn.webcodein.workshop/actions/workflows/build.yml)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-2.0.2-purple.svg)](https://testcontainers.com/)
[![ArchUnit](https://img.shields.io/badge/ArchUnit-1.4.1-red.svg)](https://www.archunit.org/)

Companion workshop code for the comprehensive guide:  
[Build an OTA Firmware Delivery Server with Spring Boot 4: Step-by-Step Tutorial](https://learn.webcodein.com/post/building-ota-firmware-delivery-server-spring-boot)

---

## Architectural Problem & Solution

Over-The-Air (OTA) firmware delivery is the backbone of modern IoT, automotive, and smart device ecosystems. Unlike standard file downloads, delivering firmware binaries to embedded hardware presents unique constraints:
1. **Unstable Network Connectivity**: Cellular and satellite connections drop frequently. Full redownloads waste device battery and network bandwidth.
2. **Resumable HTTP Byte-Ranges (RFC 7233)**: The server must advertise `Accept-Ranges: bytes` and stream partial content slices on demand.
3. **Cryptographic Integrity**: Every update payload requires strict SHA-256 checksums and digital signature validation before flashing to flash memory.
4. **Phased Rollouts & A/B Safety**: Canary deployment rings protect fleets from bricking. Devices flash passive partitions (A/B slots) and execute bootloader watchdog checks before committing.

---

## Technical Stack

| Component | Technology | Version |
|---|---|---|
| Language | OpenJDK / GraalVM | Java 25 |
| Framework | Spring Boot | 4.0.0 GA |
| Persistence | Spring Data JPA / Hibernate | 7.1 |
| Database | PostgreSQL | 16-alpine |
| Integration Testing | Testcontainers | 2.0.2 |
| Architectural Fitness | ArchUnit | 1.4.1 |

---

## Project Structure

```
building-ota-firmware-delivery-server-spring-boot/
├── pom.xml
├── compose.yaml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/webcodein/firmware/
    │   │   ├── FirmwareApplication.java           # Spring Boot entry point
    │   │   ├── model/
    │   │   │   └── Firmware.java                  # JPA entity for firmware metadata (@Table firmware_updates)
    │   │   ├── repository/
    │   │   │   └── FirmwareRepository.java        # Spring Data repository for model querying
    │   │   ├── service/
    │   │   │   └── FirmwareService.java           # Storage resolution and Resource streaming service
    │   │   └── controller/
    │   │       └── FirmwareController.java        # REST API (/api/firmware/latest, /api/firmware/download)
    │   └── resources/
    │       └── application.properties             # Database connection and storage settings
    └── test/
        └── java/com/webcodein/firmware/
            ├── model/
            │   └── FirmwareTest.java              # Entity invariant unit tests (2 tests)
            ├── service/
            │   └── FirmwareServiceTest.java       # Storage loading and exception handling tests (5 tests)
            ├── architecture/
            │   └── FirmwareArchitectureTest.java  # ArchUnit architectural governance checks (5 rules)
            └── controller/
                └── FirmwareControllerTest.java    # Full Testcontainers PostgreSQL + MockMvc tests (3 tests)
```

---

## Quickstart & Local Execution

### 1. Build and Run Tests

```bash
mvn clean test
```

Executes 15 automated tests covering:
- Firmware entity instantiation and immutability invariants
- Storage loading, resource creation, and exception handling
- WebMvc REST endpoints (`/api/firmware/latest` and binary streaming `/api/firmware/download/{id}`)
- `Accept-Ranges: bytes` header verification
- ArchUnit architectural fitness checks
- Real PostgreSQL 16 database transactions via Testcontainers

### 2. Run via Docker Compose

```bash
docker compose up --build
```

### 3. REST API Usage

#### Check for the Latest Firmware Update
```bash
curl -X GET "http://localhost:8080/api/firmware/latest?modelName=PhoneX"
```

Response (`200 OK`):
```json
{
  "id": "e3a8933b-8f0a-4221-a477-8c707db9cf26",
  "version": "2.0.0",
  "modelName": "PhoneX",
  "downloadUrl": "update_v2.bin",
  "checksum": "checksum123",
  "fileSize": 5120,
  "releaseDate": "2026-11-12T02:00:00Z",
  "mandatory": true
}
```

#### Download Firmware Binary (Resumable Streaming)
```bash
curl -i -X GET "http://localhost:8080/api/firmware/download/e3a8933b-8f0a-4221-a477-8c707db9cf26"
```
