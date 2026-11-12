# Kotlin Backend Development with Spring Boot 4

Companion workshop project for the technical guide: [Kotlin Backend Development with Spring Boot 4](https://learn.webcodein.com/post/kotlin-spring-boot-4-backend-guide).

## Architectural Overview

This project showcases modern backend development pairing **Kotlin 2.0** with **Spring Boot 4.0.0** and **Java 25**:

1. **Uncompromising Null Safety**: Leveraging Kotlin's non-nullable types (`String`) vs nullable types (`String?`) across REST controllers, services, and DTO contracts to eliminate `NullPointerException` at compile time.
2. **Data Classes for Idiomatic DTOs**: Automated generation of `copy()`, `equals()`, `hashCode()`, and component destructuring (`componentN()`) with Jackson Kotlin Module deserialization.
3. **Extension Functions**: Enriching domain models with idiomatic utility functions (`User.toSummary()`) without inheritance overhead.
4. **Spring Compiler Plugins**: Using `kotlin-maven-plugin` with `kotlin-spring` (all-open) to automatically open classes annotated with Spring stereotypes for CGLIB proxies.
5. **REST API Layer**: Exposing Spring MVC endpoints for greetings, user CRUD operations, and formatted domain summaries.

---

## Package Structure

```
com.webcodein.kotlin
├── HelloController.kt         # REST controller with MessageResponse data class
├── KotlinApplication.kt       # Spring Boot 4 application entry point
└── user/
    └── UserService.kt         # User domain model, CreateUserRequest DTO,
                               # toSummary() extension function, UserService,
                               # and UserController REST endpoints
```

---

## Test Suite Breakdown

The repository contains an exhaustive 16-test suite combining unit tests, Spring MVC slice tests, and ArchUnit architecture verification rules:

| Test Class | Scope / Target | Tests | Description |
|---|---|---|---|
| `KotlinBackendArchitectureTest` | ArchUnit 1.4.1 | 5 | Enforces `@RestController` on controllers, `@Service` on services, package placement in `user`, and eliminates `System.out`/`System.err` and `java.util.logging`. |
| `MessageResponseTest` | Unit / Data Class | 2 | Verifies data class immutability, default arguments (`author = "WebCodein"`), destructuring declarations, and Jackson Kotlin roundtrip serialization. |
| `UserServiceTest` | Unit / Service | 3 | Verifies null-safety guarantees on `findById()` / `findByEmail()`, user persistence with optional bio, and extension function formatting. |
| `HelloControllerTest` | Web Slice (`@WebMvcTest`) | 1 | Verifies `GET /api/hello` returning HTTP 200 with JSON payload. |
| `UserControllerTest` | Web Slice (`@WebMvcTest`) | 4 | Verifies `GET /api/users` (200), `GET /api/users/{id}` (200 & 404), and `POST /api/users` (201 Created). |
| `KotlinApplicationTests` | Integration / Context | 1 | Verifies Spring Boot 4 application context startup with Kotlin reflection. |

---

## Prerequisites

- **JDK 25** (Oracle OpenJDK or Eclipse Temurin)
- **Maven 3.9+** (or included `./mvnw`)
- **Docker Desktop** (optional, for containerized execution)

---

## Building and Testing

Run the full verification suite:

```bash
mvn clean test
```

Or using the Unix-formatted Maven wrapper:

```bash
./mvnw clean test
```

---

## Running the Application Locally

Start the application with Maven:

```bash
mvn spring-boot:run
```

### Example REST API Requests

```bash
# Get greeting
curl -s "http://localhost:8080/api/hello"

# List all users
curl -s "http://localhost:8080/api/users"

# Fetch a user by ID
curl -s "http://localhost:8080/api/users/1"

# Fetch a user summary via extension function
curl -s "http://localhost:8080/api/users/1/summary"

# Create a new user
curl -s -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{"username":"linus","email":"linus@kernel.org","bio":"Linux creator"}'
```

---

## Related Articles

- [Kotlin Backend Development with Spring Boot 4](https://learn.webcodein.com/post/kotlin-spring-boot-4-backend-guide)
- [What is Spring Boot? A Complete Guide to Spring Boot 4 in 2026](https://learn.webcodein.com/post/what-is-spring-boot-a-complete-guide-to-spring-boot-4-in-2026)
- [GraalVM Native Image with Spring Boot 4: Cloud-Native Java Guide](https://learn.webcodein.com/post/graalvm-native-image-spring-boot-4-cloud-native-java-guide)
- [Building Modular Monoliths with Spring Modulith and Spring Boot 4](https://learn.webcodein.com/post/spring-modulith-modular-monoliths-spring-boot-4)
