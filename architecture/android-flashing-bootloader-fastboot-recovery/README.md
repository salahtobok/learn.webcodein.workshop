# Device Flashing Audit Service (Spring Boot 4)

Complete companion repository for the technical article: [The Architecture of Safely Flashing a Phone: Bootloaders, Fastboot, and Recovery](https://learn.webcodein.com/post/android-flashing-bootloader-fastboot-recovery).

---

## Overview

This is a Spring Boot 4 / Java 25 microservice simulating a centralized audit log for devices being flashed (e.g. in manufacturing lines, enterprise fleets, or repair centers using Fastboot). It uses PostgreSQL via Spring Data JPA and Testcontainers for automated verification.

### Key Architecture Features
- **Flash Audit Logging:** Captures device serial numbers, target partition names, cryptographic image checksums, and flashing outcomes.
- **Device History Tracking:** Queries immutable historical flashing records per device hardware identifier.
- **Containerized Integration Testing:** Uses PostgreSQL 16 Alpine via Testcontainers for isolated testing.

---

## Tech Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.0
- **Database:** PostgreSQL 16 (via Testcontainers)
- **Testing:** JUnit 5, AssertJ, ArchUnit 1.4.1, Testcontainers 2.0.2

---

## Running the Tests

Execute the comprehensive test suite (ArchUnit architecture rules and containerized PostgreSQL integration tests):

```bash
./mvnw clean verify
```

### Included Test Suites
- **`FlashArchitectureTest`:** 4 ArchUnit 1.4.1 architectural fitness rules enforcing:
  1. `controllersShouldResideInControllerPackage`: REST controllers reside in `flashing.controller`.
  2. `entitiesShouldResideInModelPackage`: JPA entities reside in `flashing.model`.
  3. `repositoriesShouldBeInterfaces`: Spring Data repositories are interfaces.
  4. `controllersShouldNotDependOnOtherControllers`: Controllers are decoupled from other controllers.
- **`FlashAuditControllerTest`:** MockMvc slice tests verified against containerized PostgreSQL 16 verifying event logging and device history retrieval.
