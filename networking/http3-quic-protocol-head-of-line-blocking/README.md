# HTTP/3 and QUIC: Solving TCP Head-of-Line Blocking in Java 25

Companion workshop code for the technical article:
**"HTTP/3 and QUIC Protocol Deep Dive: Solving TCP Head-of-Line Blocking in Modern Web Architectures"** published on [learn.webcodein.com](https://learn.webcodein.com/post/http3-quic-protocol-head-of-line-blocking).

## Overview
This workshop project provides a Java 25 protocol simulation comparing **TCP (HTTP/2)** and **QUIC (HTTP/3)** under simulated network packet loss:
- **TCP Head-of-Line (HoL) Blocking:** Demonstrates how packet drops stall subsequent byte delivery across all multiplexed streams.
- **QUIC Independent Stream Flow:** Demonstrates how QUIC isolates stream state machines over UDP, allowing unaffected streams to deliver data without stalling.
- **ArchUnit 1.4.1 Architectural Rules:** Verifies clean package isolation, immutability of frames, and domain integrity.

## Prerequisites
- Java 25 SDK (Corretto 25 or Temurin 25)
- Maven 3.9+ or included `./mvnw`

## Running Tests
Run the full test suite with Surefire:
```bash
./mvnw clean test
```

## Running the Benchmark
Run the CLI simulation comparing TCP and QUIC under 10% packet drop:
```bash
./mvnw compile exec:java -Dexec.mainClass="com.webcodein.workshop.quic.QuicHolApplication"
```

## Running with Docker
```bash
docker compose up --build
```
