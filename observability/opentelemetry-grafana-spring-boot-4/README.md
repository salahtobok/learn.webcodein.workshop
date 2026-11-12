# Observability with OpenTelemetry and Grafana in Spring Boot 4.0

Companion workshop project for the post [Observability with OpenTelemetry and Grafana in Spring Boot 4.0](https://learn.webcodein.com/post/opentelemetry-grafana-spring-boot-4).

## Architecture Overview
This project demonstrates modern cloud-native observability in **Spring Boot 4.0.0** and **Java 25**:
- **Three Pillars of Observability:** Unified telemetry capturing Metrics (RED method via Micrometer OTLP registry), Distributed Tracing (W3C trace context propagation via `micrometer-tracing-bridge-otel`), and Structured Logging (Loki correlation).
- **OpenTelemetry Collector Pipeline:** OTLP gRPC/HTTP receivers ingest telemetry on port 4318, batch and process spans, and forward to Prometheus (metrics) and Grafana Tempo (traces).
- **Grafana LGTM Stack (`compose.yaml`):** Provisions Loki, Grafana, Tempo, and Prometheus for unified single-pane-of-glass dashboards.
- **Automated Verification with Testcontainers:** Integration tests launch a containerized OpenTelemetry Collector (`otel/opentelemetry-collector:0.104.0`) to verify telemetry export contracts and endpoint dispatch without external mocks.
- **ArchUnit 1.4.1 Architectural Rules:** Validates 5 strict rules enforcing controller packaging, annotations, service definitions, and standard stream logging hygiene.

## Tech Stack
- **Java:** 25
- **Framework:** Spring Boot 4.0.0 (Spring Framework 7.0)
- **Observability:** Micrometer Observation API, OpenTelemetry Java SDK, OTLP Exporter
- **Collectors & Backends:** OpenTelemetry Collector 0.104.0, Prometheus, Grafana Tempo, Loki
- **Architecture Verification:** ArchUnit 1.4.1
- **Testing:** JUnit 5, MockMvc, Testcontainers 2.0.2

## Test Suite & Verification Contracts
The project includes 11 automated tests executing against real containerized infrastructure:
- `ObservabilityArchitectureTest` (5 ArchUnit rules):
  - `controllersMustResideInControllerPackage`: Validates controller packaging structure.
  - `controllersMustBeAnnotatedWithRestController`: Enforces `@RestController` on API handlers.
  - `servicesMustBeAnnotatedWithService`: Enforces `@Service` on domain services.
  - `noClassesShouldAccessStandardStreams`: Eliminates `System.out`/`System.err` logging.
  - `noClassesShouldUseJavaUtilLogging`: Enforces SLF4J facade over JUL.
- `ObservabilityApplicationTests` (4 integration tests): Verifies context loading, `POST /api/orders` processing with Observation recording, `GET /api/orders/{id}` trace context propagation, and error observation recording against an active Testcontainers OTel collector.
- `OrderServiceTest` (2 unit tests): Unit-tests the Observation API and fallback error semantics.

## Build and Run Instructions

### Prerequisites
- JDK 25 installed and configured (`JAVA_HOME`).
- Maven 3.9+ (or use the included `./mvnw`).
- Docker Engine / Docker Desktop running.

### Local Development Mode
```bash
# Start the full Grafana LGTM stack and OTel Collector
docker compose up -d

# Run the Spring Boot application with live OTLP export
./mvnw spring-boot:run
```

### Run Test Suite
```bash
# Executes all 11 unit, slice, and containerized OTel Collector tests
./mvnw clean test
```
