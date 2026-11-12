# Zanzibar-Style Distributed ACL in Java with SpiceDB and Spring Boot 4

This is the companion code repository for the article **[Zanzibar-Style Distributed ACL in Java with SpiceDB and Spring Boot 4](https://learn.webcodein.com/post/zanzibar-distributed-acl-spicedb-spring-boot)** on Webcodein.

## Architecture & Technology Stack
- **Runtime:** Java 25 (LTS)
- **Framework:** Spring Boot 4.0.0 GA
- **Security:** Spring Security 7.0
- **Authorization Engine:** Authzed SpiceDB 1.30.0+ (Google Zanzibar ReBAC implementation)
- **Client Protocol:** gRPC with Protobuf (`com.authzed.api:authzed`)
- **Testing:** JUnit 5, Mockito, MockMvc, ArchUnit 1.4.1, Testcontainers SpiceDB

## Prerequisites
- Java 25 SDK
- Docker Desktop or Docker daemon running locally
- Maven 3.9+ (or included `./mvnw`)

## Running the Application Locally

1. **Start the SpiceDB server via Docker Compose:**
   ```bash
   docker-compose up -d
   ```

2. **Run the Spring Boot application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Verify Protected Document Endpoint:**
   ```bash
   # Authorized user: Alice
   curl -u alice:password http://localhost:8080/api/documents/1
   # Returns 200 OK: {"id":"1","content":"Confidential business roadmap.","accessedBy":"alice"}

   # Unauthorized user: Bob
   curl -u bob:password http://localhost:8080/api/documents/1
   # Returns 403 Forbidden: {"error":"Access Denied"}
   ```

## Running the Automated Test Suite

The test suite validates both unit components, slice controllers, ArchUnit architecture constraints, and live Testcontainers integration with an ephemeral SpiceDB instance:

```bash
mvn clean test
```

### Test Suite Structure (12 Tests Passing):
1. **`AuthorizationServiceTest` (3 tests):** Isolated unit tests mocking gRPC stubs to verify permission evaluation (`PERMISSIONSHIP_HAS_PERMISSION` / `PERMISSIONSHIP_NO_PERMISSION`) and schema registration.
2. **`DocumentControllerTest` (3 tests):** MockMvc slice tests verifying HTTP 200 for authorized users, HTTP 403 Forbidden for unauthorized users, and HTTP 401 for unauthenticated calls.
3. **`ZanzibarArchitectureTest` (4 tests):** ArchUnit 1.4.1 rules enforcing layered architectural integrity, naming standards, and prohibiting direct console stream access.
4. **`ZanzibarAclApplicationTests` (2 tests):** End-to-end integration tests using Testcontainers to launch a live SpiceDB container, bootstrap the ReBAC schema, write relation tuples, and test HTTP endpoints.
