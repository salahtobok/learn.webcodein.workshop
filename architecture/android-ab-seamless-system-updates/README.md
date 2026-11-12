# Android A/B Seamless System Updates Simulator (Spring Boot 4)

Complete companion repository for the technical article: [Deep Dive into Android A/B (Seamless) System Updates](https://learn.webcodein.com/post/android-ab-seamless-system-updates).

---

## Overview

This service simulates and verifies the dual-slot state machine powering Android A/B seamless OTA updates, implemented with **Spring Boot 4**, **Java 25**, **Spring Data JPA**, and **PostgreSQL 16**.

### Architecture Highlights
- **Dual Partition Slots**: Models `slot_a` and `slot_b` with attributes for `bootable` and `successful`.
- **Background Flashing Simulation**: Inactive partition is staged with new firmware version while the active slot continues serving traffic.
- **Atomic Slot Switching**: Simulates bootloader slot swapping on reboot.
- **Rollback Protection**: Simulates watchdog fallback if the target slot fails to mark successful after boot.

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
- **`AbOtaArchitectureTest`:** 4 ArchUnit 1.4.1 architectural fitness rules enforcing:
  1. `controllersShouldBeAnnotatedWithRestController`: REST endpoints are strictly annotated with `@RestController`.
  2. `entitiesShouldBeAnnotatedWithEntity`: Device slot state models are annotated with `@Entity`.
  3. `repositoriesShouldBeInterfaces`: Spring Data repositories are interfaces.
  4. `controllersShouldResideInAbPackage`: Controllers reside in the `ota.ab` package.
- **`AbOtaApplicationTests`:** End-to-end integration tests using containerized PostgreSQL 16 verifying happy-path staging/reboot/mark-successful lifecycle and watchdog fallback rollback.
