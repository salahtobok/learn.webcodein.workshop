# Real-Time Protocols: WebSockets vs. Server-Sent Events vs. WebTransport

Companion workshop project for the article: **WebSockets vs. SSE vs. WebTransport: Architectural Comparison for Modern Real-Time Web Applications**.

## Tech Stack
- **Language**: Java 25
- **Architecture**: Domain Model, Framing Engines, Benchmark Service, ArchUnit
- **Testing**: JUnit 5, AssertJ, ArchUnit 1.4.1
- **Spring Boot**: None (pure Java 25 runtime)

## Architecture Overview
The project models the core operational differences between modern real-time protocols:
1. **Server-Sent Events (SSE)**: Unidirectional streaming over HTTP/1.1 or HTTP/2, text-based framing (`id`, `event`, `data`), automatic browser reconnects.
2. **WebSocket (RFC 6455)**: Full-duplex bidirectional communication over TCP, binary and text frames, client-side masking.
3. **WebTransport (HTTP/3 QUIC)**: Multiplexed bidirectional and unidirectional streams plus unreliable out-of-order datagrams (RFC 9221) with zero head-of-line blocking.

## Running Tests
```bash
mvn clean test
```
