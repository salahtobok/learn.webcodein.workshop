# Master ArchUnit: Custom Rules and Conditions for Spring Boot 4

Companion workshop repository for the technical article:  
**[Master ArchUnit: Writing Custom Rules and Conditions for Spring Boot 4](https://learn.webcodein.com/post/master-archunit-custom-rules-spring-boot)**

---

## Architecture Overview

This project demonstrates how to author custom architectural fitness functions, predicates, and conditions using ArchUnit 1.4.1 in Spring Boot 4 and Java 25. Standard layering rules often fail to capture subtle domain leaks, legacy API contamination, improper proxy interception, or direct repository bypass. This workshop provides production-grade architectural rules that enforce code boundaries during continuous integration.

```
master-archunit-custom-rules-spring-boot
├── src
│   ├── main/java/com/webcodein/workshop/archunit
│   │   ├── ArchUnitCustomRulesApplication.java
│   │   ├── annotation
│   │   │   └── UseCase.java                   # Custom stereotyping annotation
│   │   ├── domain
│   │   │   └── Customer.java                  # Framework-agnostic entity (java.time)
│   │   ├── repository
│   │   │   └── CustomerRepository.java        # Spring Data JPA repository
│   │   ├── service
│   │   │   ├── CreateCustomerUseCase.java     # Use case orchestrating domain creation
│   │   │   └── GetCustomerUseCase.java        # Read-only use case
│   │   └── web
│   │       ├── CustomerController.java        # REST presentation controller
│   │       ├── CustomerCreationRequest.java   # Immutable record DTO
│   │       └── CustomerResponse.java          # Immutable record DTO
│   └── test/java/com/webcodein/workshop/archunit
│       ├── CustomArchUnitRulesTest.java       # 9 advanced custom ArchUnit rules
│       ├── domain
│       │   └── CustomerTest.java              # Domain invariant unit tests
│       ├── service
│       │   ├── CreateCustomerUseCaseTest.java # Service mock tests
│       │   └── GetCustomerUseCaseTest.java    # Service query tests
│       └── web
│           ├── CustomerControllerTest.java    # WebMvc slice testing
│           └── CustomerControllerIntegrationTest.java # Context & H2 integration
└── pom.xml
```

---

## Architectural Rules Enforced

The test suite in [`CustomArchUnitRulesTest.java`](src/test/java/com/webcodein/workshop/archunit/CustomArchUnitRulesTest.java) executes 9 automated architectural fitness checks:

1. **Use Case Web Isolation**: Classes annotated with `@UseCase` must never depend on the web presentation layer (`..web..`).
2. **Repository Encapsulation**: Repository interfaces must reside in `..repository..` and only be accessed by service use cases or other repositories.
3. **DTO Leak Prevention**: Domain entities must never depend on web DTOs (`..web..`).
4. **Domain Framework Independence**: Classes in `..domain..` must not depend on Spring Framework packages (`org.springframework..`).
5. **Legacy API Prohibition**: Custom `ArchCondition<JavaClass>` bans `java.util.Date` across the codebase, enforcing `java.time.*` (JSR-310).
6. **Constructor Injection Enforcement**: Custom `ArchCondition<JavaClass>` bans `@Autowired` on fields in service and web packages, requiring explicit constructor injection.
7. **Repository Bypass Prevention**: Custom `ArchCondition<JavaClass>` ensures `@RestController` classes never depend directly on `@Repository` interfaces.
8. **Transactional Method Visibility**: Custom `ArchCondition<JavaMethod>` validates that any method annotated with `@Transactional` is declared `public` to ensure Spring AOP proxy interception.
9. **Record DTO Requirement**: Custom `DescribedPredicate` and `ArchCondition` enforce that all web transport objects ending in `Request` or `Response` are immutable Java records.

---

## Build and Test

### Prerequisites
- **JDK 25** (or JDK 21+)
- **Maven 3.9+**

### Execution
Run the full test suite (21 unit, integration, and ArchUnit tests):

```bash
mvn clean test
```

Expected output:
```text
[INFO] Running com.webcodein.workshop.archunit.domain.CustomerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.workshop.archunit.service.CreateCustomerUseCaseTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.workshop.archunit.service.GetCustomerUseCaseTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.workshop.archunit.web.CustomerControllerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.workshop.archunit.web.CustomerControllerIntegrationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.workshop.archunit.CustomArchUnitRulesTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## Running the Application

Start the Spring Boot 4 service locally:

```bash
mvn spring-boot:run
```

### Sample HTTP Requests

**Create a customer:**
```bash
curl -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Smith"}'
```

Response:
```http
HTTP/1.1 201 Created
Location: http://localhost:8080/api/customers/1
```

**Retrieve a customer:**
```bash
curl -i http://localhost:8080/api/customers/1
```

Response:
```json
{
  "id": 1,
  "name": "Alice Smith",
  "createdAt": "2026-11-12T02:45:00"
}
```

---

## Continuous Integration

This project is registered in the GitHub Actions build matrix:  
[`.github/workflows/build.yml`](../../.github/workflows/build.yml)
