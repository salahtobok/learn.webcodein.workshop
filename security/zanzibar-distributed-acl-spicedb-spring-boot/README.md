# Zanzibar-style Distributed ACL in Java with SpiceDB and Spring Boot 4

This is the companion code for the Webcodein article on implementing Google Zanzibar-style ReBAC (Relationship-Based Access Control) using SpiceDB and Spring Boot 4.

## Prerequisites
- Java 25
- Docker & Docker Compose (for running SpiceDB locally)
- Maven 3.9+

## Running the Application

1. **Start the SpiceDB server via Docker Compose:**
   ```bash
   docker-compose up -d
   ```

2. **Build and Run the Spring Boot App:**
   ```bash
   ./mvnw spring-boot:run
   ```

## Running the Tests
The tests use Testcontainers to automatically spin up a temporary SpiceDB instance, apply the authorization schema, create sample relationships, and execute endpoint tests.

```bash
./mvnw clean verify
```

## Structure
- `SpiceDbClientConfig.java`: Configures the gRPC client to communicate with SpiceDB.
- `AuthorizationService.java`: Wraps the SpiceDB (Authzed) gRPC API to check permissions.
- `DocumentController.java`: Protects the `/api/documents/{id}` endpoint.
