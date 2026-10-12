# Android A/B Seamless System Updates Simulator (Spring Boot 4)

Companion code for the WebCodein article: [Android A/B (Seamless) System Updates](https://learn.webcodein.com/post/android-ab-seamless-system-updates).

This service demonstrates and simulates the dual-slot state machine powering Android A/B seamless OTA updates, implemented with Spring Boot 4, Spring Data JPA, and PostgreSQL.

## Architecture Highlights
- **Dual Partition Slots**: Models `slot_a` and `slot_b` with attributes for `bootable` and `successful`.
- **Background Flashing Simulation**: Inactive partition is staged with new firmware version while the active slot continues serving traffic.
- **Atomic Slot Switching**: Simulates bootloader slot swapping on reboot.
- **Rollback Protection**: Simulates watchdog fallback if the target slot fails to mark successful after boot.

## Prerequisites
- Java 25
- Docker (for Testcontainers PostgreSQL)

## Running Locally

Build the project using the included Maven wrapper:
```bash
./mvnw clean package
```

Run the unit and integration tests (uses Testcontainers to start PostgreSQL automatically):
```bash
./mvnw test
```

## Running via Docker
```bash
docker build -t android-ab-seamless-system-updates .
docker run -p 8080:8080 android-ab-seamless-system-updates
```
