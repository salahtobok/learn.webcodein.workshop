# Strategy Pattern Masterclass in Java 25 & Jakarta EE 11

Companion workshop code for the technical guide: **The Ultimate Masterclass: Strategy Pattern in Java 25 & Jakarta EE 11**.

This project demonstrates how to implement an enterprise-grade Strategy Design Pattern using Java 25 records, Spring Boot 4.0.0, and Jakarta Inject (`@Named`, `@Inject`) dependency injection. It showcases dynamic runtime strategy resolution, capability filtering, and structural architecture governance via ArchUnit.

---

## Architectural Highlights

- **Dynamic Dependency Injection:** Spring Boot collects all beans implementing `PaymentStrategy` and injects them into `PaymentProcessor` as an immutable `List<PaymentStrategy>`.
- **Predicate Capability Routing:** Each strategy implements `boolean supports(PaymentType)` to self-declare execution capabilities without coupling the processor to concrete implementations.
- **Modern Java 25 Records:** Data transfer payloads (`PaymentRequest`, `PaymentResult`) use immutable records with strict typing.
- **Fail-Safe Fallbacks:** Unmatched payment types throw explicit runtime exceptions mapped to HTTP 400 Bad Request responses.
- **Architectural Governance:** ArchUnit 1.4.1 test suite verifying that domain models remain framework-independent and strategies reside exclusively in the infrastructure layer.

---

## Technical Stack

- **Java:** OpenJDK 25
- **Framework:** Spring Boot 4.0.0 GA
- **Dependency Injection:** Jakarta Inject (`jakarta.inject:jakarta.inject-api:2.0.1`)
- **JSON Processing:** Jackson Databind 2.18
- **Testing:** JUnit 5, AssertJ, Mockito, Spring Boot Test / MockMvc
- **Architecture Governance:** ArchUnit JUnit 5 1.4.1

---

## Project Structure

```
strategy-pattern-java-jakarta-ee-guide/
??? pom.xml
??? src/
    ??? main/
    ?   ??? java/com/webcodein/workshop/strategy/
    ?       ??? StrategyApplication.java          # Spring Boot main entry point
    ?       ??? application/
    ?       ?   ??? PaymentProcessor.java         # Context registry collecting and executing strategies
    ?       ??? domain/
    ?       ?   ??? PaymentRequest.java           # Immutable Java 25 record for payment details
    ?       ?   ??? PaymentResult.java            # Immutable Java 25 record for transaction results
    ?       ?   ??? PaymentStrategy.java          # Core strategy behavioral interface
    ?       ?   ??? PaymentType.java              # Payment channel enumeration
    ?       ??? infrastructure/
    ?       ?   ??? CreditCardPaymentStrategy.java# Credit Card implementation (@Named)
    ?       ?   ??? PayPalPaymentStrategy.java    # PayPal implementation (@Named)
    ?       ??? presentation/
    ?           ??? PaymentController.java        # REST presentation controller: POST /api/payments
    ??? test/
        ??? java/com/webcodein/workshop/strategy/
            ??? StrategyArchitectureTest.java     # 5 ArchUnit architecture & layer rules
            ??? application/
            ?   ??? PaymentProcessorTest.java     # Unit tests for strategy selection & error handling
            ??? domain/
            ?   ??? PaymentRequestTest.java       # Unit tests for immutable domain records
            ??? infrastructure/
            ?   ??? CreditCardPaymentStrategyTest.java # Credit card strategy unit tests
            ?   ??? PayPalPaymentStrategyTest.java     # PayPal strategy unit tests
            ??? presentation/
                ??? PaymentControllerTest.java    # MockMvc controller slice tests
```

---

## Building and Testing

### Prerequisites
- JDK 25 installed and configured on your `PATH`.

### Run Test Suite
To run all unit tests, ArchUnit governance rules, and MockMvc slice tests:

```bash
mvn clean test
```

### Build Runnable Jar
```bash
mvn clean package -DskipTests
```

---

## API Usage Examples

### 1. Process Credit Card Payment

```bash
curl -X POST "http://localhost:8080/api/payments" \
     -H "Content-Type: application/json" \
     -d '{
       "amount": 100.00,
       "type": "CREDIT_CARD",
       "customerId": "CUST-123",
       "paymentDetails": "4111-2222-3333-4444"
     }'
```

**Response (HTTP 200 OK):**
```json
{
  "success": true,
  "transactionId": "d1354fc3-a9d2-4328-b0a3-f09d84bf8e71",
  "message": "Credit Card payment successful"
}
```

### 2. Process PayPal Payment

```bash
curl -X POST "http://localhost:8080/api/payments" \
     -H "Content-Type: application/json" \
     -d '{
       "amount": 50.00,
       "type": "PAYPAL",
       "customerId": "CUST-456",
       "paymentDetails": "user@example.com"
     }'
```

**Response (HTTP 200 OK):**
```json
{
  "success": true,
  "transactionId": "PP-9a40e2cf-0744-41d7-b8c7-c79f90f23d11",
  "message": "PayPal transaction completed"
}
```

### 3. Unsupported Strategy Type (Crypto)

```bash
curl -i -X POST "http://localhost:8080/api/payments" \
     -H "Content-Type: application/json" \
     -d '{
       "amount": 1.5,
       "type": "CRYPTO",
       "customerId": "CUST-789",
       "paymentDetails": "0x71C...B29"
     }'
```

**Response (HTTP 400 Bad Request):**
```json
{
  "success": false,
  "transactionId": null,
  "message": "No payment strategy found for type: CRYPTO"
}
```
