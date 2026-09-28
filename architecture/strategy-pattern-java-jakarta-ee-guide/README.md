# Strategy Pattern Masterclass in Java 25 & Jakarta EE 11

Companion code for the article: [The Ultimate Masterclass: Strategy Pattern in Java 25 & Jakarta EE 11](https://learn.webcodein.com/post/strategy-pattern-java-jakarta-ee-guide).

This project demonstrates how to implement a modern Strategy Pattern using:
- Java 25 records and modern features
- Spring Boot 4.0.0
- Jakarta EE / CDI annotations (`@Named`, `@Inject`)
- Dynamic strategy resolution via `List<PaymentStrategy>`

## Prerequisites
- Java 25
- Docker (optional, for containerized running)

## Running Locally

1. Build the project:
   ```bash
   ./mvnw clean package
   ```
2. Run tests:
   ```bash
   ./mvnw test
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Running via Docker

1. Build the image:
   ```bash
   docker build -t strategy-pattern-demo .
   ```
2. Run the container:
   ```bash
   docker run -p 8080:8080 strategy-pattern-demo
   ```

## Example Requests

**Credit Card Payment:**
```bash
curl -X POST http://localhost:8080/api/payments \
-H "Content-Type: application/json" \
-d '{"amount": 100.00, "type": "CREDIT_CARD", "customerId": "CUST-123", "paymentDetails": "1234-5678"}'
```

**PayPal Payment:**
```bash
curl -X POST http://localhost:8080/api/payments \
-H "Content-Type: application/json" \
-d '{"amount": 50.00, "type": "PAYPAL", "customerId": "CUST-456", "paymentDetails": "user@example.com"}'
```
