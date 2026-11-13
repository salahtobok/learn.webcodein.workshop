# Java 25 HttpClient and Virtual Threads: High-Throughput Networking

Companion workshop project for the article: **Modern Java 25 HttpClient and Virtual Threads: High-Throughput Asynchronous Networking Without Reactive Complexity**.

## Tech Stack
- **Language**: Java 25
- **Architecture**: Domain Model, Resilient HttpClient Factory, Scatter-Gather Gateway, ArchUnit
- **Testing**: JUnit 5, AssertJ, ArchUnit 1.4.1, JDK embedded `HttpServer`
- **Spring Boot**: None (pure Java 25 runtime)

## Architecture Overview
The project models modern Java 25 asynchronous networking patterns:
1. **Virtual Thread Per Task Executor**: Binding Java 25 virtual threads to `HttpClient` for non-blocking I/O with simple synchronous blocking code semantics.
2. **Scatter-Gather Fan-Out Gateway**: Executing concurrent requests to multiple microservices in parallel with bounded timeouts.
3. **HTTP/2 Multiplexing & Connection Pooling**: Optimizing connection reuse and stream concurrency.
4. **Resilient Failure Handling**: Graceful degradation when downstream dependencies fail.

## Running Tests
```bash
mvn clean test
```
