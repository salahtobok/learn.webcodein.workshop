# Production Kubernetes Readiness for Spring Boot 4: Graceful Shutdown, Actuator Probes, and Cgroups v2 on Java 25

Companion workshop project for the article: **"Production Kubernetes Readiness for Spring Boot 4: Graceful Shutdown, Actuator Probes, and Cgroups v2 on Java 25"**.

## Architecture Overview
This project demonstrates zero-downtime rolling updates, graceful shutdown, and container resource sizing in Spring Boot 4 on Java 25:
- **Zero-Downtime PreStop Hook**: A 15-second PreStop sleep command allows Kubernetes endpoints controllers and ingresses to deregister the dying pod before the JVM receives SIGTERM.
- **Graceful Shutdown**: `server.shutdown=graceful` with a 30-second shutdown phase timeout ensures in-flight requests complete cleanly.
- **Actuator Probes Separation**: Strict separation between Startup, Liveness, and Readiness probes preventing cascading restarts.
- **Linux Cgroups v2 Ergonomics**: Proper container sizing (`-XX:+UseContainerSupport`, `-XX:MaxRAMPercentage=75.0`, `-XX:+ExitOnOutOfMemoryError`) to eliminate silent container OOMKills.
- **ArchUnit 1.4.1 Fitness Rules**: Automated architectural governance enforcing package and layer boundaries.

## Prerequisites
- **Java 25** (JDK)
- **Maven 3.9+** (or use `./mvnw`)
- **Docker** (optional, for containerization)

## Build and Test
Run the full test suite with Maven:
```bash
./mvnw clean test
```

## Running the Application
Start the Spring Boot application locally:
```bash
./mvnw spring-boot:run
```

Check probe statuses:
```bash
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness
```

Trigger simulated PreStop drain:
```bash
curl -X POST http://localhost:8080/api/probes/drain
```
