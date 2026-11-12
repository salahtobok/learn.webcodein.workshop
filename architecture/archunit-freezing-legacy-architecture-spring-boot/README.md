# Freezing Legacy Architecture Violations with ArchUnit and Spring Boot 4

Companion code repository for the production engineering guide on freezing legacy architecture violations using ArchUnit in Spring Boot 4 and Java 25.

## Architectural Overview

Legacy enterprise codebases often accumulate architectural debt:
- Controllers calling other controllers or repositories directly.
- Domain services bypassing boundary layers or accessing web-tier components.
- Monolithic utility classes creating circular dependencies across domain packages.

Enforcing strict architectural boundaries on legacy systems with standard ArchUnit rules causes hundreds of build failures immediately, stalling development velocity.

**ArchUnit Freezing** (`FreezingArchRule`) solves this problem:
1. **Records Existing Violations**: Captures all existing rule violations into a persistent baseline violation store (`src/test/resources/archunit-violations/`).
2. **Permits Existing Debt**: Legacy violations do not fail the build.
3. **Rejects New Violations**: Any new code violating the rule immediately fails the CI pipeline.
4. **Ratchet Down (Decreasing Debt)**: When legacy code is refactored and violations decrease, ArchUnit automatically updates or requires updating the store, preventing regressions.

```
       +---------------------------------------------+
       | Legacy Spring Boot Codebase (Java 25)       |
       +---------------------------------------------+
                              |
                              v
                  [ArchUnit Test Execution]
                              |
               +--------------+--------------+
               |                             |
               v                             v
       [Frozen Layer Rules]          [Active Strict Rules]
       (FreezingArchRule)             (Strict ArchRule)
               |                             |
               +--------------+              |
                              |              |
               Does code touch old debt?     |
               [Check Violations Store]      |
               /                      \      |
      In baseline?                New debt?  |
         [PASS]                     [FAIL]   |
                                       \     /
                                        v   v
                                   [CI Pipeline]
```

---

## Project Structure

```
archunit-freezing-legacy-architecture-spring-boot/
├── pom.xml                                    # Java 25, Spring Boot 4.0.0, ArchUnit 1.4.1
├── src/
│   ├── main/
│   │   └── java/com/webcodein/legacy/
│   │       ├── LegacyApplication.java         # Spring Boot 4 application runner
│   │       ├── order/
│   │       │   └── OrderService.java          # Service with deliberate legacy violations
│   │       ├── user/
│   │       │   └── UserController.java        # Controller with deliberate direct service calls
│   │       └── util/
│   │           └── LegacyGlobalHelper.java    # God-object helper class with cross-layer leakage
│   └── test/
│       ├── java/com/webcodein/legacy/
│       │   ├── ArchitectureTest.java          # ArchUnit test suite (frozen & active rules)
│       │   ├── LegacyApplicationTests.java    # Spring Boot context boot test
│       │   ├── order/
│       │   │   └── OrderServiceTest.java      # Order service unit tests
│       │   ├── user/
│       │   │   └── UserControllerTest.java    # WebMvc slice tests (MockMvc)
│       │   └── util/
│       │       └── LegacyGlobalHelperTest.java# Helper utility unit tests
│       └── resources/
│           ├── archunit.properties            # ArchUnit freezing configuration
│           └── archunit-violations/           # Baseline stored rules and violation hashes
│               ├── stored.rules
│               ├── 641dddde-c81a-4929-814c-054a4566f67b
│               └── dd914367-772f-40c5-b7ce-c8c19c866c61
└── README.md
```

---

## ArchUnit Configuration (`archunit.properties`)

```properties
# Freeze store configuration
freeze.store.default.allowStoreCreation=true
freeze.store.default.path=src/test/resources/archunit-violations

# To regenerate the baseline when intentionally refactoring:
# freeze.refreeze=true
```

---

## Deliberate Legacy Violations Demonstrated

1. **Service to Controller Inversion**: `OrderService` accesses `UserController` to retrieve session data, violating layer hierarchy.
2. **God Utility Leakage**: `LegacyGlobalHelper` bridges controllers, domain services, and raw JSON parsing without clean boundaries.
3. **Circular Package Dependencies**: Cross-domain calls between `order` and `user` packages without port interfaces.

All of these deliberate violations are recorded in `src/test/resources/archunit-violations/` and frozen under `ArchitectureTest.java`.

---

## Test Suite Execution

Run the complete test suite using Apache Maven:

```bash
mvn clean test
```

### Verified Test Suite (8/8 Passed)

- **`ArchitectureTest`** (4 tests):
  - `layered_architecture_should_be_respected`: Layer boundaries frozen via `FreezingArchRule`.
  - `services_should_not_depend_on_controllers`: Inversion violation frozen via `FreezingArchRule`.
  - `no_classes_should_throw_generic_exceptions`: Active strict rule enforced across all code.
  - `spring_controllers_must_have_controller_or_rest_controller_annotation`: Active naming and stereotype rule.
- **`LegacyApplicationTests`** (1 test): Spring Boot 4 application context initialization.
- **`UserControllerTest`** (1 test): MockMvc web slice test verifying HTTP 200 `/api/users/profile`.
- **`OrderServiceTest`** (1 test): Order checkout and discount calculation verification.
- **`LegacyGlobalHelperTest`** (1 test): Utility helper verification.

---

## Tech Stack
- **Java**: 25 (OpenJDK)
- **Framework**: Spring Boot 4.0.0 GA
- **Architecture Testing**: ArchUnit 1.4.1 (`archunit-junit5`)
- **Testing**: JUnit 5, AssertJ, Spring Test MockMvc
