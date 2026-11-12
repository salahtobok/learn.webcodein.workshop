# Workshop: Jakarta Security 4.0 vs Spring Security 7: The Ultimate 2026 Guide

Companion code for the article: [Jakarta Security 4.0 vs Spring Security 7: The Ultimate 2026 Guide](https://learn.webcodein.com/post/jakarta-security-4-vs-spring-security-7).

This repository demonstrates the same secured REST API implemented in two contrasting enterprise Java paradigms:

| Folder | Stack | What it shows |
|--------|-------|---------------|
| `spring-security/` | Java 25, Spring Boot 4.0.0, Spring Security 7.0.1 | Functional Lambda DSL `SecurityFilterChain`, JWT OAuth2 Resource Server, ArchUnit architecture guardrails, and MockMvc slice tests. |
| `jakarta-security/` | Java 25, Jakarta EE 11 (Jakarta Security 4.0) | Standard declarative annotations (`@OpenIdAuthenticationMechanismDefinition`, `@RolesAllowed`), MicroProfile JWT, and CDI-managed `SecurityContext`. |

---

## Requirements
- **JDK 25**
- **Maven 3.9+** (or use included `./mvnw`)
- Compatible runtime for Jakarta EE 11 (WildFly 33+, Payara 7, Open Liberty 24+, or Quarkus 3.x)

---

## Automated Verification & Testing

Both projects contain automated verification suites running on JDK 25:

### 1. Spring Security 7 Subproject:
```bash
cd spring-security
mvn clean test
```
**Test Coverage (10/10 tests pass):**
- `ApiControllerTest`: 5 MockMvc slice tests validating unauthenticated rejections (HTTP 401), role-based authorization (HTTP 403 on missing `ROLE_ADMIN`), successful access with mock JWT (HTTP 200), and public endpoints.
- `SecurityDemoApplicationTests`: 1 test verifying full Spring Boot application context bootstrap.
- `SpringSecurityArchitectureTest`: 4 ArchUnit rules enforcing controller/config annotations, naming conventions, and logging hygiene.

### 2. Jakarta Security 4.0 Subproject:
```bash
cd jakarta-security
mvn clean test
mvn clean package   # Produces target/jakarta-security-4-demo.war
```
**Test Coverage (1/1 test passes):**
- `AdminResourceTest`: Unit validation of JAX-RS declarative authorization contracts and role checking.

---

## Architectural Comparison Matrix

| Feature | Jakarta Security 4.0 | Spring Security 7 |
| :--- | :--- | :--- |
| **Model** | Declarative CDI annotations & IdentityStore | Centralized `SecurityFilterChain` Lambda DSL |
| **Specification** | Jakarta EE 11 (Eclipse Foundation) | Spring Framework (Broadcom / VMware) |
| **OIDC / JWT** | `@OpenIdAuthenticationMechanismDefinition` & MicroProfile JWT | `oauth2ResourceServer().jwt(...)` |
| **GraalVM Native** | Build-time metadata reflection / runtime proxies | Spring AOT ahead-of-time code generation |
| **Method Security** | `@RolesAllowed`, `@PermitAll`, `@DenyAll` | SpEL `@PreAuthorize`, `@PostAuthorize`, ABAC |
