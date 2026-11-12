# Event-Driven Microservices with Apache Kafka & Spring Boot 4

Companion code for the article: [Event-Driven Microservices in Java 25: Apache Kafka & Spring Boot 4](https://learn.webcodein.com/post/event-driven-kafka-spring-boot-4)

## Prerequisites
- Java 25
- Docker and Docker Compose
- Maven (or use the provided `mvnw`)

## Running Locally (Development)
The project is configured with `spring-boot-docker-compose`, which will automatically start a local Kafka container for you when you run the app.

```bash
./mvnw clean spring-boot:run
```

Once running, you can test the endpoints:
```bash
# Produce a new order event
curl -X POST "http://localhost:8080/api/orders?amount=99.99"

# Consume and view processed orders
curl "http://localhost:8080/api/orders"
```

## Testing Suite
The repository includes comprehensive automated tests running on Java 25:
- **Testcontainers Integration Testing (`KafkaDemoApplicationTests`)**: Boots an ephemeral Kafka broker in Docker using `confluentinc/cp-kafka:7.7.1` and verifies producer-to-consumer event streams.
- **Controller Unit Tests (`OrderControllerTest`)**: Mocks services with Mockito to validate REST request parsing and payload responses.
- **ArchUnit Architectural Rules (`KafkaArchitectureTest`)**: Uses ArchUnit 1.4.1 to enforce clean layer isolation, service packaging, and component naming conventions.

Run the test suite with:
```bash
./mvnw clean test
```
