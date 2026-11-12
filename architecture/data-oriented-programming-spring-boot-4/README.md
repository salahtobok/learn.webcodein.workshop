# Data-Oriented Programming in Spring Boot 4 & Java 25

Companion code repository for the production engineering guide on applying Data-Oriented Programming (DOP) principles in Spring Boot 4 using modern Java 25 features: Records, Sealed Interfaces, and Exhaustive Pattern Matching.

---

## Architectural Overview

Object-Oriented Programming (OOP) binds data and behavior together inside classes, frequently hiding internal state behind mutable fields. In high-concurrency microservices, mutable hidden state leads to subtle race conditions, complex synchronization, and fragile testing.

**Data-Oriented Programming (DOP)** takes an alternative approach:
1. **Model Data as Passive Data**: Use Java `record` components for transparent, immutable data containers without getters, setters, or hidden fields.
2. **Make Illegal States Unrepresentable**: Use `sealed interface` hierarchies with strictly permitted subtypes, enabling compile-time exhaustiveness checking.
3. **Separate Code from Data**: Keep business rules inside stateless `@Service` components. Use pattern matching `switch` expressions with type patterns and guards (`when` clauses).
4. **Transparent Serialization**: Seamlessly map polymorphic JSON payloads to sealed domain hierarchies using Jackson `@JsonTypeInfo` and `@JsonSubTypes`.

```
     Client HTTP Request (JSON)
                |
                v
      [Jackson Deserializer]
  (@JsonTypeInfo / @JsonSubTypes)
                |
                v
     +------------------------------------------+
     | Sealed Type Hierarchy: PaymentMethod     |
     |  ├── CreditCard (record)                 |
     |  ├── PayPal (record)                     |
     |  └── Crypto (record)                     |
     +------------------------------------------+
                |
                v
     [PaymentProcessingService]
     (Exhaustive Pattern Matching Switch)
       case CreditCard cc -> ...
       case PayPal pp -> ...
       case Crypto cr when BTC -> ...
       case Crypto cr -> ...
                |
                v
     Immutable Response String
```

---

## Project Structure

```
data-oriented-programming-spring-boot-4/
├── pom.xml                                    # Java 25, Spring Boot 4.0.0 GA, ArchUnit 1.4.1
├── src/
│   ├── main/
│   │   └── java/com/webcodein/dop/
│   │       ├── DopApplication.java            # Spring Boot application runner
│   │       ├── controller/
│   │       │   └── PaymentController.java     # REST presentation layer (/api/payments/process)
│   │       ├── model/
│   │       │   ├── PaymentMethod.java         # Sealed domain interface (permits CreditCard, PayPal, Crypto)
│   │       │   ├── CreditCard.java            # Immutable record with compact constructor validation
│   │       │   ├── PayPal.java                # Immutable record with email validation
│   │       │   ├── Crypto.java                # Immutable record with address and currency validation
│   │       │   └── PaymentRequest.java        # Immutable request record (amount + PaymentMethod)
│   │       └── service/
│   │           └── PaymentProcessingService.java # Business service using exhaustive switch & guards
│   └── test/
│       └── java/com/webcodein/dop/
│           ├── DopApplicationTests.java       # Context initialization & integration test
│           ├── DopArchitectureTest.java       # 6 ArchUnit architecture governance rules
│           ├── controller/
│           │   └── PaymentControllerTest.java # WebMvc slice tests with polymorphic JSON payloads
│           ├── model/
│           │   ├── CreditCardTest.java        # Record invariant & constructor tests
│           │   ├── CryptoTest.java            # Crypto record invariant tests
│           │   ├── PayPalTest.java            # PayPal record invariant tests
│           │   └── PaymentRequestTest.java    # Request validation tests
│           └── service/
│               └── PaymentProcessingServiceTest.java # Unit tests for all pattern-matching cases
└── README.md
```

---

## Verification Test Suite (28 / 28 Tests Passed)

Execute the full verification test suite using Apache Maven:

```bash
mvn clean test
```

### Test Breakdown

1. **`DopArchitectureTest`** (6 tests):
   - `models_should_be_records_or_interfaces`: Enforces that domain data structures remain transparent records or interfaces.
   - `models_should_not_depend_on_services_or_controllers`: Ensures complete isolation of data structures from application logic.
   - `services_should_not_depend_on_controllers`: Maintains strict inward layer dependencies.
   - `services_must_be_annotated_with_service`: Enforces Spring component stereotypes.
   - `controllers_must_have_rest_controller_annotation`: Standardizes REST endpoints.
   - `no_classes_should_throw_generic_exceptions`: Forbids generic exceptions in favor of precise domain exceptions.
2. **`PaymentControllerTest`** (4 tests):
   - `shouldProcessCreditCardEndpoint`: Validates JSON polymorphic deserialization for credit cards.
   - `shouldProcessPayPalEndpoint`: Validates PayPal JSON payload.
   - `shouldProcessBitcoinEndpoint`: Validates Crypto BTC JSON payload.
   - `shouldRejectInvalidJsonWithoutType`: Verifies HTTP 400 Bad Request on missing polymorphic discriminator.
3. **`PaymentProcessingServiceTest`** (4 tests):
   - Exhaustive pattern matching verification for `CreditCard`, `PayPal`, `Crypto (BTC)` with guard clause, and `Crypto (ETH)` default branch.
4. **`Model Unit Tests`** (13 tests):
   - Invariant validation, precondition checks in compact constructors, equality, and hash codes across `CreditCardTest`, `CryptoTest`, `PayPalTest`, and `PaymentRequestTest`.
5. **`DopApplicationTests`** (1 test):
   - Full Spring Boot 4 application context boot and end-to-end payment pipeline verification.

---

## Running the Application Locally

```bash
mvn spring-boot:run
```

Submit a sample payment request via curl:

```bash
curl -X POST http://localhost:8080/api/payments/process \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 250.0,
    "method": {
      "type": "creditCard",
      "cardNumber": "4111222233334444",
      "expiryDate": "12/28"
    }
  }'
```

---

## Tech Stack
- **Java**: 25 (OpenJDK)
- **Framework**: Spring Boot 4.0.0 GA
- **JSON Serialization**: Jackson 2.18+ (Polymorphic `@JsonTypeInfo` and `@JsonSubTypes`)
- **Testing**: JUnit 5, AssertJ, Spring Test MockMvc, ArchUnit 1.4.1
