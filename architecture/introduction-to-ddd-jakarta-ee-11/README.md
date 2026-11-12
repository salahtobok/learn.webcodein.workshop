# Introduction to Domain-Driven Design with Jakarta EE 11 & Spring Boot 4

This companion workshop project demonstrates core **Domain-Driven Design (DDD)** strategic and tactical patterns implemented with **Jakarta EE 11**, **Jakarta Data 1.0**, **Jakarta Persistence 3.2**, **Hibernate ORM 7.1**, **Java 25**, and **Spring Boot 4.0.0**.

---

## Architecture Highlights

1. **Tactical DDD Building Blocks**:
   - **Aggregate Root (`Order`)**: Protects domain invariants, encapsulates child entities, and controls lifecycle state transitions (`PENDING` -> `CONFIRMED` -> `SHIPPED`).
   - **Entities (`OrderItem`)**: Unique identity via UUID, positive quantity validation, and line total calculation.
   - **Value Objects (`Address`)**: Immutable value object with structural equality, zero identity, and strict null guards.
   - **Repository Pattern (`OrderRepository`)**: Clean abstraction managing aggregate persistence boundaries via Jakarta Data and Spring Data.
2. **Layered Domain Architecture**:
   - Clean separation across `domain`, `application`, and `web` presentation packages.
   - DTO projection boundaries preventing aggregate leakage over HTTP APIs.
3. **ArchUnit 1.4.1 Architectural Governance**:
   - Enforces package layering: domain classes cannot depend on application or web layers.
   - Forbids field injection and generic exceptions.
   - Prohibits legacy temporal types (`java.util.Date`, `java.util.Calendar`) in favor of `java.time`.
4. **Integration Testing with Testcontainers 2.0.2**:
   - Zero-mock integration tests verifying persistence, cascade mapping, and querying against real PostgreSQL 16 Alpine containers.

---

## Project Structure

```text
introduction-to-ddd-jakarta-ee-11/
├── Dockerfile                  # Container packaging definition
├── pom.xml                     # Maven project configuration (Boot 4.0.0, Java 25, Testcontainers)
├── README.md                   # Documentation and quickstart guide
└── src/
    ├── main/
    │   ├── java/com/webcodein/ddd/
    │   │   ├── DddApplication.java                 # Spring Boot application bootstrap
    │   │   ├── application/
    │   │   │   ├── dto/
    │   │   │   │   ├── AddressDto.java             # Immutable Java 25 record
    │   │   │   │   ├── CreateOrderRequest.java     # Immutable request payload record
    │   │   │   │   ├── OrderItemDto.java           # Order line item record
    │   │   │   │   └── OrderResponse.java          # Order response representation record
    │   │   │   └── service/
    │   │   │       └── OrderService.java           # Transactional application service
    │   │   ├── domain/
    │   │   │   ├── Address.java                    # Embeddable Value Object
    │   │   │   ├── Order.java                      # Aggregate Root Entity
    │   │   │   └── OrderItem.java                  # Child Entity within aggregate
    │   │   ├── infrastructure/
    │   │   │   └── OrderRepository.java            # Aggregate repository interface
    │   │   └── web/
    │   │       └── controller/
    │   │           └── OrderController.java        # REST presentation controller
    │   └── resources/
    │       └── application.properties              # Database and JPA configuration
    └── test/
        └── java/com/webcodein/ddd/
            ├── DddArchitectureTest.java            # ArchUnit DDD governance rules
            ├── OrderIntegrationTest.java           # PostgreSQL 16 Testcontainers integration
            ├── application/
            │   └── service/
            │       └── OrderServiceTest.java       # Application service unit tests
            ├── domain/
            │   ├── AddressTest.java                # Value object invariant tests
            │   ├── OrderItemTest.java              # Entity invariant and validation tests
            │   └── OrderTest.java                  # Aggregate root lifecycle tests
            └── web/
                └── controller/
                    └── OrderControllerTest.java    # MockMvc slice tests
```

---

## Build, Test & Run

### 1. Execute Unit, Slice, and Integration Tests
```bash
mvn clean test
```

### 2. Run the Application Locally
```bash
mvn spring-boot:run
```

### 3. API Verification

#### Create an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "shippingAddress": {
      "street": "100 Innovation Way",
      "city": "Austin",
      "zipCode": "78701"
    },
    "items": [
      {
        "productName": "Ergonomic Mechanical Keyboard",
        "quantity": 1,
        "price": 180.00
      },
      {
        "productName": "Precision Mouse",
        "quantity": 2,
        "price": 45.00
      }
    ]
  }'
```

#### Get Order by ID
```bash
curl -X GET http://localhost:8080/api/orders/{orderId}
```

#### Confirm Order
```bash
curl -X POST http://localhost:8080/api/orders/{orderId}/confirm
```
