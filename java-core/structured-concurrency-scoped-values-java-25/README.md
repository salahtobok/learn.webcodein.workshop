# Structured Concurrency & Scoped Values in Java 25 (Spring Boot 4)

Companion workshop repository for the tutorial: **Structured Concurrency and Scoped Values in Java 25: Replacing ThreadLocal in High-Throughput Services**.

This project demonstrates how to eliminate thread leaks, unmanaged thread sprawl, and `ThreadLocal` memory overhead in modern Spring Boot 4 applications using Java 25's **Structured Concurrency** (`StructuredTaskScope`) and **Scoped Values** (`ScopedValue`).

---

## Key Architectural Patterns

1. **Immutable Context Propagation (`ScopedValue`)**:
   - Replaces `ThreadLocal` with stack-confined, immutable, and leak-free request contexts.
   - Automatically inherited across virtual child subtasks without expensive map copying.
2. **Fail-Fast Parallel Fan-Out (`StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow()`)**:
   - Executes inventory reservation, payment processing, and fraud detection concurrently.
   - Automatically cancels running siblings when any subtask fails or times out.
3. **Speculative Redundant Racing (`StructuredTaskScope.Joiner.anySuccessfulOrThrow()`)**:
   - Queries multiple carrier endpoints simultaneously and returns the fastest quote while cancelling slower ones.

---

## Tech Stack

- **Java**: 25 (with `--enable-preview`)
- **Framework**: Spring Boot 4.0.0
- **Testing**: JUnit 5, AssertJ, Spring Boot Test, MockMvc
- **Build Tool**: Maven 3.9+

---

## Prerequisites

- JDK 25 or newer installed
- Maven 3.9+ (or use the included `./mvnw` wrapper)
- Docker (optional, for containerized execution)

---

## Building and Testing

### Run Integration Tests
```bash
./mvnw clean test
```

### Full Verification Build
```bash
./mvnw clean verify
```

All 5 integration tests verify:
- Happy-path fan-out checkout across 3 virtual threads.
- Fail-fast cancellation on fraud detection.
- Transaction rejection on payment limits.
- Speculative carrier quote racing.
- Zero cross-thread context pollution under concurrent load.

---

## Running the Application Locally

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

---

## Running with Docker Compose

```bash
docker compose up --build
```

---

## Example API Requests

### 1. High-Throughput Checkout (Fail-Fast Fan-Out)
```bash
curl -X POST http://localhost:8080/api/orders/checkout \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: corr-demo-101" \
  -H "X-User-Id: alice" \
  -H "X-Tenant-Id: tenant-prod" \
  -d '{
    "orderId": "ORD-5001",
    "customerId": "CUST-42",
    "amount": 149.99,
    "itemCount": 2
  }'
```

**Response (HTTP 201 Created):**
```json
{
  "orderId": "ORD-5001",
  "status": "CONFIRMED",
  "inventoryReservationId": "INV-A1B2C3D4",
  "paymentTransactionId": "PAY-E5F6G7H8",
  "riskScore": 12,
  "durationMs": 64,
  "correlationId": "corr-demo-101",
  "processedByThread": "VirtualThread[#84]/runnable@ForkJoinPool-1-worker-3"
}
```

### 2. Speculative Racing for Fastest Shipping Quote
```bash
curl -X GET "http://localhost:8080/api/shipping/quotes/fastest?origin=JFK&destination=LHR" \
  -H "X-Correlation-Id: corr-demo-102"
```

**Response (HTTP 200 OK):**
```json
{
  "carrierName": "FastExpress Logistics",
  "estimatedCost": 24.50,
  "transitDays": 2,
  "quoteTimestamp": "2026-10-26T00:15:00Z",
  "correlationId": "corr-demo-102"
}
```

### 3. Micro-Benchmark: ScopedValue vs ThreadLocal
```bash
curl -X GET "http://localhost:8080/api/benchmarks/scoping?iterations=100000"
```

---

## License

This project is part of the `learn.webcodein.com` companion workshop code repository and is open-sourced under the MIT License.
