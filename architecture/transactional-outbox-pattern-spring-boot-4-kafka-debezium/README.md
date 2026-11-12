# Transactional Outbox Pattern with Spring Boot 4, Kafka, and Debezium

[![Build Status](https://github.com/salahtobok/learn.webcodein.workshop/actions/workflows/build.yml/badge.svg)](https://github.com/salahtobok/learn.webcodein.workshop/actions/workflows/build.yml)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Debezium](https://img.shields.io/badge/Debezium-2.7-red.svg)](https://debezium.io/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.8-black.svg)](https://kafka.apache.org/)

Companion code for the in-depth article:  
[Implementing the Transactional Outbox Pattern with Spring Boot 4, Kafka, and Debezium](https://learn.webcodein.com/post/transactional-outbox-pattern-spring-boot-4-kafka-debezium)

---

## Architectural Problem: The Dual Write Issue

In distributed microservice systems, a single business action frequently demands two updates:
1. Mutating state in a relational database (e.g., PostgreSQL).
2. Emitting an event to a distributed message bus (e.g., Apache Kafka).

A direct or naive implementation creates a **Dual Write** risk:
```
Client Request -> Save Order to DB (OK) -> Send Event to Kafka (FAILS: Network error / Timeout)
Result: Inconsistent state! Database has the order, but downstream services never know.
```

Distributed 2-Phase Commit (XA/2PC) is notoriously slow, fragile, and unsupported by modern event brokers like Kafka.

### The Solution: Transactional Outbox with Change Data Capture (CDC)

1. **Atomic Local Commit**: The application saves both the business entity (`orders`) and an outbox event (`outbox_events`) inside a single local relational database transaction.
2. **Asynchronous CDC Tailing**: Debezium connects to PostgreSQL's Write-Ahead Log (WAL) via logical replication (`pgoutput`).
3. **Outbox Event Router SMT**: Debezium's Single Message Transformation routes the payload directly to the appropriate Kafka topic (`order.events`) partitioned by aggregate ID.
4. **Guaranteed Delivery**: No messages are lost even if Kafka, Debezium, or the application restarts.

---

## Technical Stack

| Component | Technology | Version |
|---|---|---|
| Runtime | OpenJDK / GraalVM | Java 25 |
| Framework | Spring Boot | 4.0.0 GA |
| Persistence | Spring Data JPA / Hibernate | 7.1 |
| Database | PostgreSQL (Logical Decoding) | 16-alpine / 15-alpine |
| Broker | Apache Kafka | 3.8 / Confluent Platform |
| CDC Engine | Debezium PostgreSQL Connector | 2.7.0.Final |
| Testing | Testcontainers & ArchUnit | 2.0.2 / 1.4.1 |

---

## Project Structure

```
transactional-outbox-pattern/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/webcodein/workshop/outbox/
    │   │   ├── OutboxApplication.java      # Application entrypoint
    │   │   ├── Order.java                  # JPA business entity (orders table)
    │   │   ├── OrderRepository.java        # Spring Data JPA repository
    │   │   ├── OrderService.java           # @Transactional boundary for order + outbox
    │   │   ├── OrderController.java        # REST API (/api/orders)
    │   │   ├── OutboxEvent.java            # Outbox entity (outbox_events table, JSONB payload)
    │   │   └── OutboxEventRepository.java  # Repository for outbox persistence
    │   └── resources/
    │       └── application.yml             # Data source and JPA settings
    └── test/
        └── java/com/webcodein/workshop/outbox/
            ├── OrderTest.java              # Unit invariants for Order entity
            ├── OutboxEventTest.java        # Invariants for OutboxEvent entity
            ├── OrderServiceTest.java       # Unit test verifying transactional outbox generation
            ├── OrderControllerTest.java    # WebMvc slice tests for REST endpoints
            ├── OutboxArchitectureTest.java # ArchUnit fitness checks
            └── OutboxIntegrationTest.java  # Testcontainers PostgreSQL full integration test
```

---

## Quickstart & Local Execution

### 1. Start Infrastructure via Docker Compose

```bash
docker-compose up -d
```

Services started:
- PostgreSQL on port `5432` with `wal_level=logical`
- Zookeeper on port `2181`
- Apache Kafka on port `9092`
- Debezium Kafka Connect on port `8083`

### 2. Configure the Debezium Postgres Connector

Deploy the connector with Outbox Event Router SMT:

```bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
  http://localhost:8083/connectors/ -d '{
    "name": "outbox-connector",
    "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "postgres",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "password",
      "database.dbname": "postgres",
      "database.server.name": "dbserver1",
      "table.include.list": "public.outbox_events",
      "plugin.name": "pgoutput",
      "tombstones.on.delete": "false"
    }
}'
```

### 3. Run the Spring Boot Application

```bash
./mvnw spring-boot:run
```

### 4. Create an Order via REST API

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"product": "MacBook Pro M4 Max", "price": 3499.00}'
```

Response (`201 Created`):
```json
{
  "id": 1,
  "product": "MacBook Pro M4 Max",
  "price": 3499.00
}
```

### 5. Inspect the Kafka Events

Listen to the generated Kafka event topic:

```bash
docker exec -it transactional-outbox-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic dbserver1.public.outbox_events \
  --from-beginning
```

---

## Automated Test Suite

Run the complete test suite including ArchUnit and Testcontainers:

```bash
mvn clean test
```

Included verification layers:
1. **Unit tests (`OrderTest`, `OutboxEventTest`)**: Validate constructors, immutability, and JSON schema boundaries.
2. **Service orchestration (`OrderServiceTest`)**: Ensures both `Order` and `OutboxEvent` are staged and saved together.
3. **HTTP REST slices (`OrderControllerTest`)**: Verifies `POST /api/orders` HTTP 201 and `GET /api/orders/{id}` HTTP 200/404 handling.
4. **Architecture fitness (`OutboxArchitectureTest`)**: ArchUnit verifies service and controller annotations, repository interfaces, and package integrity.
5. **Database integration (`OutboxIntegrationTest`)**: Starts real PostgreSQL inside Docker via Testcontainers and executes the transaction against real tables and JSONB types.
