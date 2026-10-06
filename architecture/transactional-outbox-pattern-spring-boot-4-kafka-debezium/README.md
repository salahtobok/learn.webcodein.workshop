# Transactional Outbox Pattern with Spring Boot 4, Kafka, and Debezium

This workshop demonstrates how to avoid the "Dual Write" problem using the **Transactional Outbox Pattern**. Instead of directly publishing an event to Kafka after saving business data (which can lead to inconsistencies if Kafka is down), we save an `OutboxEvent` to the database in the exact same transaction. 

A Change Data Capture (CDC) tool like Debezium then tails the database transaction logs (WAL) and reliably pushes those events to Kafka.

## Stack
- Java 25
- Spring Boot 4.0.0
- Spring Data JPA
- PostgreSQL 15
- Apache Kafka
- Debezium 2.7
- Testcontainers

## Running the Architecture

1. Start the infrastructure (PostgreSQL, Zookeeper, Kafka, Debezium):
   ```bash
   docker-compose up -d
   ```
2. Start the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Register the Debezium Postgres Connector via REST API:
   ```bash
   curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{
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
        "plugin.name": "pgoutput"
      }
   }'
   ```
4. Listen to the Kafka Topic:
   ```bash
   docker exec -it <kafka-container-id> kafka-console-consumer --bootstrap-server localhost:9092 --topic dbserver1.public.outbox_events --from-beginning
   ```
