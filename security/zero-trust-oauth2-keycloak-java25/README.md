# Zero-Trust OAuth2 with Keycloak (Java 25 & Spring Boot 4)

Companion workshop project for the tutorial: [Mastering Zero-Trust Architecture: Implementing OAuth2 and Keycloak in Java 25](https://learn.webcodein.com/post/zero-trust-oauth2-keycloak-java25).

This repository demonstrates how to implement a perimeterless Zero-Trust security model for Java 25 microservices using Spring Boot 4.0.0, Spring Security 7, and Keycloak 26 as the OpenID Connect (OIDC) identity provider.

## Architecture Highlights
- **Zero-Trust Paradigm**: Continuous cryptographic validation of JSON Web Tokens (JWT) for every inbound HTTP transaction.
- **Spring Security 7 Resource Server**: Custom `KeycloakGrantedAuthoritiesConverter` parsing both `realm_access.roles` and standard OIDC scopes into Spring `GrantedAuthority` objects.
- **Role-Based Authorization**: Granular RBAC enforcing `ROLE_USER` and `ROLE_ADMIN` on protected API endpoints.
- **Automated Verification**: End-to-end integration tests using Testcontainers running an ephemeral Keycloak 26 instance with automated realm import.

## Prerequisites
- Docker Engine & Docker Compose
- JDK 25
- Maven 3.9+ (or use included `./mvnw`)

---

## Running the Automated Test Suite

Run the full automated test suite (including Testcontainers Keycloak integration tests):

```bash
./mvnw clean verify
```

---

## Local Development Running Guide

### 1. Start Keycloak

Start the pre-configured Keycloak instance with Docker Compose:

```bash
docker compose up -d keycloak
```

Keycloak will start on `http://localhost:8080`.
- Admin console: `http://localhost:8080/admin` (admin / admin)
- Pre-imported realm: `webcodein-realm`
- Seeded users:
  - `testuser` / `password` (roles: `USER`, `ADMIN`)
  - `regularuser` / `password` (roles: `USER`)

### 2. Start the Spring Boot Application

Run the application locally:

```bash
./mvnw spring-boot:run
```

The resource server listens on `http://localhost:8081`.

---

### 3. Testing the Endpoints via cURL

#### Step A: Test Public Endpoint
```bash
curl -i http://localhost:8081/api/public/hello
```
*Expected: HTTP 200 OK*
```json
{"message":"Hello, this is a public endpoint."}
```

#### Step B: Test Protected Endpoint without Token
```bash
curl -i http://localhost:8081/api/secure/hello
```
*Expected: HTTP 401 Unauthorized*

#### Step C: Obtain JWT Access Token from Keycloak
```bash
TOKEN=$(curl -s -X POST "http://localhost:8080/realms/webcodein-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=spring-boot-client" \
  -d "username=testuser" \
  -d "password=password" | jq -r .access_token)
```

#### Step D: Access User Endpoint with Token
```bash
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/secure/hello
```
*Expected: HTTP 200 OK*
```json
{"message":"Hello testuser, you are authenticated via Zero-Trust OAuth2."}
```

#### Step E: Access Admin Dashboard with Admin Token
```bash
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/admin/dashboard
```
*Expected: HTTP 200 OK*
```json
{"message":"Welcome to the Admin Dashboard."}
```

---

## Clean Up

Stop and remove running containers:

```bash
docker compose down -v
```

## Related Resources
- [Mastering Zero-Trust Architecture Article](https://learn.webcodein.com/post/zero-trust-oauth2-keycloak-java25)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
