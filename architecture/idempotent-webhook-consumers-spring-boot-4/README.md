# Building Highly Reliable & Idempotent Webhook Consumers in Spring Boot 4

This is the official companion code for the Webcodein article: **Building Highly Reliable & Idempotent Webhook Consumers in Spring Boot 4**.

This project demonstrates how to build an enterprise-grade, cryptographically secure, and strictly idempotent webhook consumer in Spring Boot 4 and Java 25 using PostgreSQL and asynchronous background processing.

---

## Architectural Highlights

- **HMAC-SHA256 Cryptographic Verification:** Protects webhook endpoints against spoofing attacks using constant-time digest comparison (`MessageDigest.isEqual`) to eliminate timing attacks.
- **Strict Idempotency Gateway:** Enforces atomic deduplication at the database boundary via unique `event_id` primary key constraints, returning immediate HTTP 200/202 acknowledgements to stop upstream retry storms.
- **Decoupled Asynchronous Processing:** Ingestion is decoupled from business execution, persisting incoming events in `< 50ms` and delegating heavy operations to scheduled background processors.
- **Resilient State Machine:** Webhook events transition through deterministic states: `RECEIVED -> PROCESSING -> PROCESSED` (or `FAILED` with retry handling).
- **ArchUnit Architectural Governance:** 5 ArchUnit rules enforcing strict package conventions, annotation requirements, and layer boundaries.

---

## Project Structure

```text
com.webcodein.webhook/
├── WebhookApplication.java              # Spring Boot application entry point & @EnableScheduling
├── controller/
│   └── WebhookController.java           # Ingestion endpoint verifying HMAC and ensuring idempotency
├── model/
│   ├── EventStatus.java                 # Enum representing RECEIVED, PROCESSED, FAILED states
│   └── WebhookEvent.java                # JPA Entity storing event_id, payload, and status
├── repository/
│   └── WebhookEventRepository.java      # Spring Data JPA repository for webhook event records
├── security/
│   └── HmacSignatureVerifier.java       # Constant-time HMAC-SHA256 cryptographic verification utility
├── service/
│   └── WebhookProcessor.java            # Background service asynchronously processing pending webhooks
└── (test)
    ├── WebhookIntegrationTest.java      # End-to-end integration test on PostgreSQL 16 Testcontainers
    ├── controller/
    │   └── WebhookControllerTest.java   # MockMvc slice tests for ingestion validation & status codes
    ├── model/
    │   └── WebhookEventTest.java        # Unit tests verifying domain model transitions
    ├── security/
    │   └── HmacSignatureVerifierTest.java # Unit tests for HMAC calculation and tamper resistance
    └── architecture/
        └── WebhookArchitectureTest.java # 5 ArchUnit fitness rules enforcing layer and naming governance
```

---

## Prerequisites

- Java 25
- Maven 3.9+
- Docker & Docker Desktop (for PostgreSQL Testcontainers)

---

## Running the Application Locally

1. **Configure Webhook Secret in `application.properties`:**
   ```properties
   webhook.secret=whsec_your_stripe_or_github_webhook_secret
   ```

2. **Run the Spring Boot Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Simulate Inbound Webhook via cURL:**
   ```bash
   # Calculate HMAC-SHA256 hex signature for payload
   PAYLOAD='{"id":"evt_test_001","type":"payment_intent.succeeded"}'
   SIGNATURE=$(echo -n "$PAYLOAD" | openssl dgst -sha256 -hmac "whsec_your_stripe_or_github_webhook_secret" | sed 's/^.* //')

   # Dispatch webhook request
   curl -X POST "http://localhost:8080/api/webhooks/stripe" \
        -H "Content-Type: application/json" \
        -H "Stripe-Signature: $SIGNATURE" \
        -d "$PAYLOAD"
   ```

---

## Automated Test Suite

Run the full automated test suite containing 21 tests across unit, MockMvc slice, ArchUnit, and Testcontainers:

```bash
./mvnw clean test
```

Test Breakdown:
- **`WebhookControllerTest` (5 tests):** Validates HTTP 202 Accepted on new events, HTTP 200 OK on duplicate events, HTTP 200 OK on concurrent insertion conflicts, HTTP 401 on signature mismatches, and HTTP 400 on malformed payloads.
- **`WebhookIntegrationTest` (3 tests):** Validates end-to-end HTTP client delivery, asynchronous transition to `PROCESSED`, duplicate rejection, and signature enforcement on a live PostgreSQL 16 Testcontainer.
- **`HmacSignatureVerifierTest` (5 tests):** Validates correct HMAC generation, detection of payload tampering, mismatched secrets, corrupted headers, and empty signatures.
- **`WebhookEventTest` (3 tests):** Validates default `RECEIVED` status and subsequent state transitions.
- **`WebhookArchitectureTest` (5 rules):** Validates controller and service package placements, annotations (`@RestController`, `@Service`, `@Entity`), and layer isolation.
