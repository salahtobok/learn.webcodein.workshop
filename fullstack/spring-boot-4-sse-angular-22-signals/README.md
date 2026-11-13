# Server-Sent Events (SSE) with Spring Boot 4 Virtual Threads & Angular 22 Signals

Companion code repository for the article: **"Real-Time Push Streaming: Server-Sent Events (SSE) with Spring Boot 4 Virtual Threads and Angular 22 Signals"**.

## Architecture Overview

This project demonstrates how to build ultra-low-latency, high-concurrency real-time push streaming applications combining Spring Boot 4.0.0, Java 25 Virtual Threads, and Angular 22 Zoneless Signals.

Key architectural features:
- **Scalable Push Streaming with SseEmitter**: Delivers unidirectional real-time price updates over standard HTTP/2 streams (`text/event-stream`).
- **Java 25 Virtual Threads**: Eliminates traditional thread pool starvation, allowing tens of thousands of concurrent open connections on minimal heap memory.
- **Angular 22 Signals & Zoneless Reactivity**: Direct streaming from browser EventSource into fine-grained reactive Signal graphs, repainting specific DOM elements with zero Zone.js overhead.
- **Heartbeats & Auto-Reconnection**: Emits periodic comment heartbeats to maintain middlebox proxy state, while supporting `Last-Event-ID` recovery.
- **ArchUnit 1.4.1 Governance**: Automated architectural rules ensuring package boundary compliance and record immutability.

## Technology Stack

- **Java**: 25 (OpenJDK / Temurin)
- **Framework**: Spring Boot 4.0.0
- **Frontend Paradigm**: Angular 22 Signals & Zoneless change detection
- **Testing**: JUnit 5, AssertJ, Spring WebMvc Test (MockMvc), ArchUnit 1.4.1
- **Build**: Maven 3.9+

## Running Tests

Execute all unit, MockMvc slice, and ArchUnit architecture tests:

```bash
mvn test
```

Expected output:
```text
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Running the Application

```bash
./mvnw spring-boot:run
```

Subscribe to the live SSE price stream:
```bash
curl -N http://localhost:8080/api/stream/prices
```

Broadcast a price update:
```bash
curl -i -X POST http://localhost:8080/api/stream/publish \
  -H "Content-Type: application/json" \
  -d '{"ticker":"NVDA","price":138.50,"changePercent":2.4,"sequenceNumber":1001}'
```
