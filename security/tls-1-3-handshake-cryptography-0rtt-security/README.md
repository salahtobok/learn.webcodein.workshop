# TLS 1.3 Handshake Cryptography & 0-RTT Security Engine

Pure Java 25 workshop project demonstrating the inner cryptographic mechanics of **TLS 1.3 (RFC 8446)** and **0-RTT Early Data session resumption**.

## Architecture & Concepts

1. **Key Exchange (ECDHE / X25519)**:
   - Modern forward secrecy through ephemeral Diffie-Hellman key exchanges (`KeyExchangeEngine`).
   - Deprecated static RSA key exchange is completely removed.
2. **Key Schedule Derivation (HKDF)**:
   - Implementation of RFC 5869 and RFC 8446 Section 7.1 (`HkdfEngine`).
   - Step-by-step extraction and expansion: Early Secret -> Handshake Secret -> Master Secret -> Traffic Keys.
3. **AEAD Encryption (AES-GCM)**:
   - Authenticated Encryption with Associated Data (`AeadCipherEngine`).
4. **0-RTT Early Data Security & Anti-Replay Mitigation**:
   - RFC 8446 Section 8 protections against replay attacks (`EarlyDataSecurityManager`).
   - Single-use session ticket tracking and anti-replay cache.
   - Freshness window validation (clock skew limits).
   - Safe HTTP method enforcement (rejecting 0-RTT for state-mutating requests like `POST`).
5. **Architectural Verification**:
   - Pure Java 25 with ArchUnit governance rules (`TlsArchitectureTest`).

## Running Tests

```bash
mvn clean test
```
