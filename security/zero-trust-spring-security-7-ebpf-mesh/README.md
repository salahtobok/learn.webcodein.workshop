# Zero-Trust Microservices with Spring Security 7 & eBPF Service Meshes

Companion workshop project for the tutorial: [Zero-Trust Architecture in Spring Boot 4 with Spring Security 7](https://learn.webcodein.com/post/zero-trust-spring-security-7-ebpf-mesh).

This repository demonstrates how to architect a defense-in-depth Zero-Trust microservice using Java 25, Spring Boot 4.0.0, Spring Security 7, and sidecarless eBPF service meshes (such as Cilium or Istio Ambient Mesh).

## Architectural Model: Defense in Depth
- **Layer 4 / Network Layer (eBPF Kernel Hooks)**: Transparent, sidecarless Mutual TLS (mTLS) encryption, packet filtering, and identity verification performed at the Linux kernel socket layer (`sockops` / `tc`).
- **Layer 7 / Application Layer (Spring Security 7)**: Continuous verification of JSON Web Tokens (JWT), extracting delegated scopes (`SCOPE_finance:read`) and evaluating fine-grained method security (`@PreAuthorize`) against the authenticated subject.

---

## Prerequisites
- JDK 25
- Maven 3.9+ (or use included `./mvnw`)
- Docker & Docker Compose (optional, for local Keycloak)

---

## Running the Automated Test Suite

Run the full automated test suite verifying both unit logic and security filter chain enforcement:

```bash
./mvnw clean verify
```

### Verified Test Contracts:
1. `healthEndpointPermitAll`: Unauthenticated access to `/actuator/health` returns `HTTP 200 OK` (`{"status":"UP"}`).
2. `balanceEndpointRejectsUnauthenticated`: Unauthenticated access to protected `/api/v1/accounts/{accountId}/balance` returns `HTTP 401 Unauthorized`.
3. `balanceEndpointRejectsMissingScope`: Requests with valid JWT lacking `SCOPE_finance:read` return `HTTP 403 Forbidden`.
4. `balanceEndpointRejectsNonOwner`: Requests where JWT subject does not match the `{accountId}` URL parameter return `HTTP 403 Forbidden`.
5. `balanceEndpointAllowsOwnerWithFinanceScope`: Requests where JWT has both `SCOPE_finance:read` and matching owner subject return `HTTP 200 OK` with balance payload.

---

## Running the Application Locally

```bash
./mvnw spring-boot:run
```

The service runs on `http://localhost:8080`.

---

## Related Resources
- [Zero-Trust Architecture in Spring Boot 4 & Spring Security 7 Article](https://learn.webcodein.com/post/zero-trust-spring-security-7-ebpf-mesh)
- [Mastering Zero-Trust Architecture: Keycloak in Java 25](https://learn.webcodein.com/post/zero-trust-oauth2-keycloak-java25)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [eBPF Foundation](https://ebpf.io/)
