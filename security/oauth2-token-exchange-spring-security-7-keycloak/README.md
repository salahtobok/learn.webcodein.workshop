# OAuth 2.0 Token Exchange (RFC 8693) with Spring Security 7 & Keycloak in Spring Boot 4

Companion code repository for the article: **"Zero-Trust Identity Propagation: OAuth 2.0 Token Exchange (RFC 8693) with Spring Security 7 and Keycloak"**.

## Architecture Overview

This project implements the OAuth 2.0 Token Exchange standard (RFC 8693) for Zero-Trust microservice environments using Spring Boot 4.0.0, Spring Security 7, and Java 25.

Key architectural features:
- **RFC 8693 Compliance**: Standardized `/oauth/token` exchange endpoint accepting `urn:ietf:params:oauth:grant-type:token-exchange` payloads.
- **Delegation vs. Impersonation**: Preserves cryptographic audit chains by maintaining `sub` (subject user) and `act` (actor service) claims.
- **Privilege Attenuation (Downscoping)**: Restricts token scopes to the absolute minimum required for each downstream service hop.
- **Security Audit Trail**: In-memory and persistent cryptographic audit log recording subject, actor, and audience details.
- **ArchUnit 1.4.1 Governance**: Automated architectural rules ensuring package boundary compliance and record immutability.

## Technology Stack

- **Java**: 25 (OpenJDK / Temurin)
- **Framework**: Spring Boot 4.0.0
- **Testing**: JUnit 5, AssertJ, Spring WebMvc Test (MockMvc), ArchUnit 1.4.1
- **Build**: Maven 3.9+

## Running Tests

Execute all unit, MockMvc slice, and ArchUnit architecture tests:

```bash
mvn test
```

Expected output:
```text
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Running the Application

```bash
./mvnw spring-boot:run
```

Execute an RFC 8693 Token Exchange:
```bash
curl -i -X POST http://localhost:8080/oauth/token \
  -H "Content-Type: application/json" \
  -d '{
    "grantType": "urn:ietf:params:oauth:grant-type:token-exchange",
    "subjectToken": "jwt_token_alice",
    "subjectTokenType": "urn:ietf:params:oauth:token-type:jwt",
    "actorToken": "jwt_token_gateway",
    "actorTokenType": "urn:ietf:params:oauth:token-type:jwt",
    "audience": "payment-service",
    "scope": "payments:transfer"
  }'
```

Inspect Security Audit Trail:
```bash
curl -i http://localhost:8080/admin/token-exchange/audit
```
