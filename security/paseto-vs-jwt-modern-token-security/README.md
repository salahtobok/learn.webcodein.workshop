# PASETO vs. JWT: Modern Token Security in Java 25

Companion workshop code for the technical article:
**"PASETO vs. JWT: Why Modern Security Architectures Are Replacing JOSE and JSON Web Tokens"** published on [learn.webcodein.com](https://learn.webcodein.com/post/paseto-vs-jwt-modern-token-security).

## Overview
This workshop provides an enterprise cryptographic implementation demonstrating why **PASETO (Platform-Agnostic Security Tokens)** eliminates the attack vectors of **JOSE / JWT**:
- **JWT Alg Confusion Exploit:** Demonstrates how header-controlled cipher negotiation (`"alg": "none"` and RSA-to-HMAC confusion) compromises authentication.
- **PASETO v4.public:** Implements asymmetric token signing using Ed25519 (RFC 8032) and Pre-Authentication Encoding (PAE) to prevent canonicalization attacks.
- **PASETO v4.local:** Implements authenticated symmetric encryption using ChaCha20-Poly1305.
- **ArchUnit 1.4.1 Architectural Rules:** Verifies cryptographic encapsulation and package decoupling.

## Prerequisites
- Java 25 SDK (Corretto 25 or Temurin 25)
- Maven 3.9+ or included `./mvnw`

## Running Tests
Run all unit and architecture tests:
```bash
./mvnw clean test
```

## Running the Security Demo
```bash
./mvnw compile exec:java -Dexec.mainClass="com.webcodein.workshop.paseto.PasetoSecurityApplication"
```

## Running with Docker
```bash
docker compose up --build
```
