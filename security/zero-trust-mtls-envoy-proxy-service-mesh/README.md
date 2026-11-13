# Zero-Trust mTLS with Envoy Proxy & SPIFFE Identity in Java 25

Companion workshop code for the technical article:
**"Zero-Trust mTLS with Envoy Proxy: End-to-End Service-to-Service Encryption and Identity"** published on [learn.webcodein.com](https://learn.webcodein.com/post/zero-trust-mtls-envoy-proxy-service-mesh).

## Overview
This workshop project provides a Java 25 implementation of Zero-Trust service-to-service communication using mutual TLS (mTLS) and SPIFFE X.509 SVIDs:
- **SPIFFE Certificate Authority:** Issues X.509 certificates with Subject Alternative Names (SAN) encoding SPIFFE IDs (`spiffe://cluster.local/ns/prod/sa/order-service`).
- **mTLS Policy Engine:** Validates client certificate authenticity, trust domains, and inter-service authorization rules.
- **Envoy Proxy Configuration (`envoy/envoy.yaml`):** Production Envoy DownstreamTlsContext requiring client certificates and matching typed SANs.
- **ArchUnit 1.4.1 Architectural Governance:** Enforces clean separation of identity models, engine, and service layers.

## Prerequisites
- Java 25 SDK (Corretto 25 or Temurin 25)
- Maven 3.9+ or included `./mvnw`

## Running Tests
Run all unit and architecture tests:
```bash
./mvnw clean test
```

## Running the Security Demonstration
```bash
./mvnw compile exec:java -Dexec.mainClass="com.webcodein.workshop.mtls.MtlsMeshApplication"
```

## Running with Docker
```bash
docker compose up --build
```
