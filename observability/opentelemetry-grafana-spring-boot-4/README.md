# Observability with OpenTelemetry and Grafana in Spring Boot 4.0

This is the companion code for the Webcodein article: [Observability with OpenTelemetry and Grafana in Spring Boot 4.0](https://learn.webcodein.com/post/opentelemetry-grafana-spring-boot-4).

## Overview
This project demonstrates how to set up distributed tracing, metrics, and logs in a Spring Boot 4 application using Micrometer and OpenTelemetry (OTLP), exporting data to an OpenTelemetry Collector, Prometheus, Tempo, Loki, and Grafana.

## Prerequisites
- Java 25
- Docker & Docker Compose
- Maven 3.9+

## Running Locally

1. **Start the Observability Stack:**
   ```bash
   docker-compose up -d
   ```
   This starts Grafana (3000), Prometheus (9090), Tempo (3200), Loki (3100), and the OTel Collector.

2. **Run the Spring Boot Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Generate Traffic:**
   Trigger the API to generate traces and metrics:
   ```bash
   curl -X POST "http://localhost:8080/api/orders?id=123"
   ```

4. **View Dashboards:**
   Open Grafana at `http://localhost:3000`. Navigate to Explore and query Prometheus or Tempo to see the exported data.

## Build and Test
Run tests with Testcontainers:
```bash
./mvnw clean verify
```
