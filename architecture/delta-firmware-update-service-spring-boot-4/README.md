# Delta Firmware Update Service (Spring Boot 4)

Companion workshop code for the technical guide: **Building a Delta Firmware Update (Patching) Service in Spring Boot 4**.

This project implements an enterprise-grade Over-The-Air (OTA) differential update orchestration service using Java 25, Spring Boot 4.0.0, Spring Data JPA, and PostgreSQL. It demonstrates how to determine differential update paths (`bsdiff`), compute cryptographic signatures, and stream differential patches to edge IoT devices.

---

## Architectural Highlights

- **Binary Difference Resolution:** Pre-computes and maps byte-level diffs (`bsdiff`/`Courgette`) to deliver 95% bandwidth savings over monolithic full-ROM payloads.
- **Fail-Safe Fallback Mechanics:** Dynamically evaluates version graphs; if an intermediate delta patch is unavailable, seamlessly provides the authoritative full-ROM payload.
- **Cryptographic Guardrails:** Issues SHA-256 payload digests for edge verification before partition flashing.
- **Containerized Integration Testing:** Powered by Spring Boot 4 `@ServiceConnection` and Testcontainers PostgreSQL 16.
- **Structural Governance:** ArchUnit 1.4.1 test suite verifying architectural boundaries, naming conventions, and modern `java.time` usage.

---

## Technical Stack

- **Java:** OpenJDK 25
- **Framework:** Spring Boot 4.0.0
- **Persistence:** Spring Data JPA, Hibernate 7
- **Database:** PostgreSQL 16
- **Architecture Testing:** ArchUnit JUnit 5 1.4.1
- **Integration Testing:** Testcontainers 2.0.2

---

## Project Structure

```
delta-firmware-update-service-spring-boot-4/
??? pom.xml
??? src/
    ??? main/
    ?   ??? java/com/webcodein/workshop/ota/
    ?   ?   ??? DeltaPatch.java                   # Differential patch entity with SHA-256 and byte sizes
    ?   ?   ??? DeltaPatchRepository.java         # Lookup repository for version-to-version patches
    ?   ?   ??? FirmwareVersion.java              # Canonical firmware release entity
    ?   ?   ??? FirmwareVersionRepository.java    # Release catalog queries
    ?   ?   ??? OtaServiceApplication.java        # Spring Boot main entry point
    ?   ?   ??? OtaUpdateController.java          # REST endpoint: GET /api/v1/ota/check
    ?   ?   ??? UpdateResponse.java               # Record payload with target version, URL, hash, and size
    ?   ?   ??? UpdateService.java                # Patch selection and full-ROM fallback engine
    ?   ??? resources/
    ?       ??? application.yml                   # Database & application configuration
    ??? test/
        ??? java/com/webcodein/workshop/ota/
            ??? DeltaPatchTest.java               # Domain entity unit tests
            ??? FirmwareVersionTest.java          # Version entity unit tests
            ??? OtaArchitectureTest.java          # ArchUnit structural governance rules
            ??? OtaUpdateControllerIntegrationTest.java # Full PostgreSQL Testcontainers integration test
            ??? OtaUpdateControllerTest.java      # MockMvc standalone controller tests
            ??? UpdateServiceTest.java            # Service business logic and fallback unit tests
```

---

## Building and Testing

### Prerequisites
- JDK 25 installed and available on `PATH`
- Docker Desktop or a running Docker daemon (for Testcontainers)

### Run Test Suite
To run all unit tests, ArchUnit governance rules, and Testcontainers integration tests:

```bash
mvn clean test
```

### Build Runnable Jar
```bash
mvn clean package -DskipTests
```

---

## API Usage Examples

### 1. Check for Update (Delta Patch Available)

```bash
curl -X GET "http://localhost:8080/api/v1/ota/check?currentVersion=1.1.0&deviceModel=PhoneX" \
     -H "Accept: application/json"
```

**Response (HTTP 200 OK):**
```json
{
  "targetVersion": "1.2.0",
  "downloadUrl": "https://s3.amazonaws.com/ota-bucket/patch-1.1-to-1.2.bsdiff",
  "patchSha256": "sha-patch-1.1-1.2",
  "sizeBytes": 18000000,
  "isCritical": true
}
```

### 2. Device Already Up to Date

```bash
curl -i -X GET "http://localhost:8080/api/v1/ota/check?currentVersion=1.2.0&deviceModel=PhoneX"
```

**Response:**
```
HTTP/1.1 204 No Content
```
