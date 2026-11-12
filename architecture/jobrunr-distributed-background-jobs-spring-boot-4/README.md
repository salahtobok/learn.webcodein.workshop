# JobRunr Distributed Background Jobs with Spring Boot 4

This is the official companion code for the Webcodein article: **Ultimate Guide to Distributed Background Jobs in Java 25 with JobRunr & Spring Boot 4**.

This project demonstrates how to set up resilient, distributed background processing in Spring Boot 4 and Java 25 using JobRunr, PostgreSQL, and Virtual Threads (Project Loom).

---

## Architectural Highlights

- **Persistent Job Storage:** Uses PostgreSQL (`jobrunr_jobs`, `jobrunr_backgroundjobservers`) for distributed state management and optimistic concurrency locking.
- **Java 25 Virtual Threads:** High-throughput background execution pool processing hundreds of concurrent I/O-bound jobs with low memory footprint.
- **Automated Failover & ZooKeeper Election:** Heartbeat renewal and automatic recovery of orphan jobs when worker nodes crash.
- **Fluent API Scheduling:** Fire-and-forget, delayed (`schedule`), and recurring (`scheduleRecurrently`) background tasks.
- **Architectural Fitness Rules:** Enforced via ArchUnit 1.4.1 to maintain clean layered architecture boundaries.

---

## Project Structure

```text
com.webcodein.jobrunr/
├── JobRunrApplication.java           # Spring Boot entry point & JobRunr bootstrap configuration
├── controller/
│   └── JobController.java            # REST API for enqueuing and scheduling background tasks
├── service/
│   └── EmailService.java             # Background job worker annotated with @Job
└── (test)
    ├── JobRunrApplicationTests.java  # Full context integration test with PostgreSQL Testcontainers
    ├── controller/
    │   └── JobControllerTest.java    # MockMvc slice tests for job submission endpoints
    ├── service/
    │   └── EmailServiceTest.java     # Unit tests for background business execution
    └── architecture/
        └── JobRunrArchitectureTest.java # 5 ArchUnit rules enforcing package and layer governance
```

---

## Prerequisites

- Java 25 (or Java 21+ with Virtual Threads)
- Maven 3.9+
- Docker & Docker Desktop (for PostgreSQL and Testcontainers)

---

## Running the Application Locally

1. **Start PostgreSQL via Docker Compose:**
   ```bash
   docker-compose up -d postgres
   ```

2. **Run the Spring Boot Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access JobRunr Embedded Dashboard:**
   Open `http://localhost:8000/dashboard` in your browser.

4. **Trigger Background Jobs via cURL:**

   - **Fire-and-forget job:**
     ```bash
     curl -X POST "http://localhost:8080/api/jobs/enqueue?email=developer@webcodein.com"
     ```

   - **Delayed job (scheduled 1 minute in the future):**
     ```bash
     curl -X POST "http://localhost:8080/api/jobs/schedule"
     ```

   - **Recurring job (cron scheduled daily):**
     ```bash
     curl -X POST "http://localhost:8080/api/jobs/recurring"
     ```

---

## Automated Testing Suite

The project includes 16 automated tests covering unit, WebMvc slice, ArchUnit governance, and Testcontainers PostgreSQL integration:

```bash
./mvnw clean test
```

Test breakdown:
- **`JobControllerTest` (5 tests):** Validates HTTP 200 responses, missing parameter validation (HTTP 400), and invalid email formatting.
- **`JobRunrApplicationTests` (4 tests):** Tests full application context initialization and live job scheduling on ephemeral PostgreSQL 16 Testcontainers.
- **`EmailServiceTest` (2 tests):** Tests isolated execution of background email dispatch and monthly report generation.
- **`JobRunrArchitectureTest` (5 rules):** Validates package structure, annotations (`@RestController`, `@Service`), and strict layer isolation.
