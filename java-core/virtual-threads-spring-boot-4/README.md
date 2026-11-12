# Java 25 Virtual Threads in Spring Boot 4 Workshop

Companion workshop repository for the tutorial: [**Java 25 Virtual Threads: Production Pitfalls and Best Practices for Spring Boot**](https://learn.webcodein.com/post/java-25-virtual-threads-production-pitfalls-and-best-practices-for-spring-boot).

This project demonstrates how to eliminate thread starvation, avoid carrier thread pinning, and handle thousands of concurrent blocking operations using **Java 25 Virtual Threads** (`Project Loom`) in **Spring Boot 4.0.0** without the complexity of Reactive WebFlux.

---

## Key Architectural Patterns

1. **Virtual Thread Execution (`spring.threads.virtual.enabled=true`)**:
   - Replaces traditional platform worker pools with disposable, user-mode virtual threads.
   - Automatically reconfigures Tomcat, async tasks, and schedulers for cooperative continuation yielding.
2. **Carrier Pinning Prevention (`ReentrantLock`)**:
   - Replaces `synchronized` monitor locks guarding I/O boundaries with `java.util.concurrent.locks.ReentrantLock`, allowing carrier threads to unmount safely during blocking pauses.
3. **High-Concurrency Task Fan-Out**:
   - Dispatches parallel subtasks across virtual threads using `Executors.newVirtualThreadPerTaskExecutor()`.
4. **Architectural Guardrails (`ArchUnit 1.4.1`)**:
   - Enforces package stratification, naming conventions, and layer independence across API and Service tiers.

---

## Tech Stack

- **Java**: 25 LTS
- **Framework**: Spring Boot 4.0.0
- **Testing**: JUnit 5, AssertJ, Spring Boot Test, MockMvc, ArchUnit 1.4.1
- **Build Tool**: Maven 3.9+

---

## Prerequisites

- JDK 25 or newer installed
- Maven 3.9+ (or use the included `./mvnw` wrapper)
- Docker (optional, for containerized execution)

---

## Building and Testing

### Run All Tests (Integration + Architectural)
```bash
./mvnw clean test
```

### Full Verification Build
```bash
./mvnw clean verify
```

All 9 automated tests pass 100%:
- **4 Integration Tests (`VirtualThreadsIntegrationTest`)**:
  - Single report generation endpoint returns HTTP 200 and Success payload.
  - Batch report endpoint spawns tasks concurrently on virtual threads (`isVirtual=true`).
  - Lock safety endpoint verifies unpinned execution with `ReentrantLock`.
  - Concurrent burst of 20 parallel client requests completes without thread exhaustion.
- **5 Architectural Tests (`VirtualThreadsArchitectureTest`)**:
  - `controllersShouldResideInApiPackage`: Controllers reside strictly in `..api..`.
  - `controllersShouldBeAnnotatedWithRestController`: Controller classes carry `@RestController`.
  - `servicesShouldResideInServicePackage`: Services reside strictly in `..service..`.
  - `servicesShouldBeAnnotatedWithService`: Service classes carry `@Service`.
  - `servicesShouldNotDependOnControllers`: Clean layer boundary isolation.

---

## Running the Application Locally

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

---

## Example API Requests

### 1. Blocking Report Generation (Virtual Thread Yielding)
```bash
curl -X GET http://localhost:8080/api/reports/generate
```

**Response (HTTP 200 OK):**
```json
{
  "status": "Success",
  "message": "Report generated successfully!",
  "isVirtual": true,
  "thread": "VirtualThread[#46,tomcat-handler-1]/runnable@ForkJoinPool-1-worker-2"
}
```

### 2. High-Concurrency Batch Dispatch
```bash
curl -X GET "http://localhost:8080/api/reports/batch?count=5"
```

### 3. Carrier Lock Safety Verification
```bash
curl -X GET http://localhost:8080/api/reports/lock-safety
```

**Response (HTTP 200 OK):**
```json
{
  "status": "Success",
  "lockType": "ReentrantLock",
  "isVirtual": true,
  "pinned": false
}
```

---

## Running via Docker

```bash
docker build -t virtual-threads-spring-boot-4 .
docker run -p 8080:8080 virtual-threads-spring-boot-4
```

---

## License

This project is part of the `learn.webcodein.com` companion workshop code repository and is open-sourced under the MIT License.
