# Zero-Trust Architecture: Implementing OAuth2 and Keycloak in Java 25

This is the companion code repository for the article **[Mastering Zero-Trust Architecture: Implementing OAuth2 and Keycloak in Java 25](https://learn.webcodein.com/post/zero-trust-oauth2-keycloak-java25)** on Webcodein.

## Architecture & Technology Stack
- **Runtime:** Java 25 (LTS)
- **Framework:** Spring Boot 4.0.0 GA
- **Security:** Spring Security 7.0 (OAuth2 Resource Server with Nimbus JWT)
- **Identity Provider:** Keycloak 26.0.0 (Quarkus distribution, OIDC/OAuth 2.1)
- **Testing:** JUnit 5, MockMvc, ArchUnit 1.4.1, Testcontainers Keycloak

## Prerequisites
- Java 25 SDK
- Docker Desktop or Docker daemon running locally
- Maven 3.9+ (or included `./mvnw`)

## Running Locally with Docker Compose

1. **Start the Keycloak Identity Server:**
   ```bash
   docker-compose up -d
   ```
   Keycloak will be available on `http://localhost:8080` with realm `webcodein-realm` automatically pre-loaded with credentials:
   - Admin user: `testuser` / `password` (roles: `USER`, `ADMIN`)
   - Regular user: `regularuser` / `password` (role: `USER`)

2. **Start the Spring Boot Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Verify API Endpoints:**
   ```bash
   # 1. Public endpoint (No token required -> 200 OK)
   curl http://localhost:8081/api/public/hello

   # 2. Secure endpoint without token -> 401 Unauthorized
   curl -i http://localhost:8081/api/secure/hello

   # 3. Obtain Access Token from Keycloak
   TOKEN=$(curl -s -X POST "http://localhost:8080/realms/webcodein-realm/protocol/openid-connect/token" \
     -d "client_id=spring-boot-client" \
     -d "grant_type=password" \
     -d "username=testuser" \
     -d "password=password" | jq -r .access_token)

   # 4. Access Admin Endpoint with Valid Token -> 200 OK
   curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/admin/dashboard
   ```

## Running the Automated Test Suite

The test suite validates both unit components, slice controllers, ArchUnit architecture constraints, and live Testcontainers integration with Keycloak 26:

```bash
mvn clean test
```

### Test Suite Structure (16 Tests Passing):
1. **`ApiControllerTest` (5 tests):** MockMvc slice tests verifying unauthenticated access, JWT authentication, and RBAC authorization (`ROLE_ADMIN` vs `ROLE_USER`).
2. **`KeycloakGrantedAuthoritiesConverterTest` (3 tests):** Isolated unit tests verifying extraction and mapping of nested `realm_access.roles` and standard OAuth2 scopes.
3. **`ZeroTrustArchitectureTest` (3 tests):** ArchUnit 1.4.1 rules enforcing layered architectural package locations, controller suffix conventions, and prohibiting direct console stream access.
4. **`ZeroTrustKeycloakIntegrationTest` (5 tests):** End-to-end integration tests using Testcontainers to launch a live Keycloak 26 instance, import `realm-export.json`, obtain real JWTs via direct access grant, and verify HTTP endpoints.
