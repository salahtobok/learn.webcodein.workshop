# High-Performance Microservice Communication: gRPC vs. REST in Spring Boot 4 and Java 25

This project demonstrates and benchmarks **gRPC with Protocol Buffers** against standard **REST over JSON** in **Spring Boot 4.0.0** and **Java 25**.

## Key Comparisons
1. **Payload Serialization**: Binary Protocol Buffers vs text-based JSON encoding.
2. **Transport Layer**: HTTP/2 multiplexed streaming vs HTTP/1.1 head-of-line blocking.
3. **Architecture Rules**: ArchUnit governance enforcing separation of concerns between controllers and services.

## Tech Stack
- Java 25
- Spring Boot 4.0.0
- ArchUnit 1.4.1
- JUnit 5 & MockMvc

## Run Tests
```bash
mvn -B -ntp clean test
```
