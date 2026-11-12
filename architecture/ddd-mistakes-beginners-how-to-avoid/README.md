# Domain-Driven Design (DDD): 10 Common Beginner Mistakes & How to Fix Them

Companion reference implementation for the article: **[DDD Mistakes Every Beginner Makes (And How to Fix Them) | Java Guide](https://learn.webcodein.com/post/ddd-mistakes-beginners-how-to-avoid)**.

This workshop demonstrates the before-and-after architectural patterns for the 10 most frequent mistakes Java engineers make when transitioning from traditional database-centric CRUD to Domain-Driven Design (DDD) in Spring Boot 4 and Java 25.

---

## Architectural Highlights

1. **Rich Domain Model vs. Anemic Entities**:
   - `Order.java` encapsulates business invariants: state transitions (`confirm()`, `cancel()`, `markAsShipped()`), non-zero item quantity enforcement, and immutable collections.
   - Eliminates leaky public setters and external transaction scripts.
2. **Simple CRUD vs. DDD Pragmatism**:
   - `TagEntity.java`: Demonstrates that simple administrative entities do not require aggregate overhead or tactical DDD patterns.
3. **Dedicated API DTO Layer**:
   - `OrderController.java` returns strongly typed Java records (`OrderResponseDTO`) rather than exposing internal domain entities to HTTP clients.
4. **Focused Aggregate Boundaries**:
   - `store.domain.Customer` and `store.domain.Order` reference each other strictly by identity (`UUID customerId`) instead of forming a massive "God Aggregate" object graph with cascading database locks.
5. **Decoupled Bounded Contexts via Domain Events**:
   - `sales.application.OrderCheckoutService` publishes `OrderConfirmedEvent` to decouple sales from logistics without direct database table joins or synchronous blocking RPCs.
6. **Automated Architectural Fitness via ArchUnit**:
   - `DddArchitectureTest.java` verifies that domain layers have zero coupling to Spring MVC web controllers or framework transport layers at compile time.

---

## Package Structure

```text
src/main/java/com/webcodein/workshop/dddmistakes/
├── Application.java                           # Spring Boot 4 entry point
├── DddArchitectureTest.java                   # ArchUnit architectural governance rules
├── cms/
│   └── entity/
│       └── TagEntity.java                     # Pragmatic CRUD entity for admin tags
├── ecommerce/
│   ├── api/controller/
│   │   ├── OrderController.java              # REST controller with 404/200 handling
│   │   └── OrderResponseDTO.java              # Immutable response record DTO
│   └── domain/
│       ├── Order.java                         # Rich aggregate root with invariant protection
│       ├── OrderItem.java                     # Value object inside order aggregate
│       ├── OrderRepository.java               # Aggregate repository interface
│       ├── OrderStatus.java                   # Lifecycle states (CREATED, CONFIRMED, SHIPPED, CANCELED)
│       └── Product.java                       # Product catalog reference model
├── logistics/
│   └── domain/
│       ├── Address.java                       # Value object for delivery addresses
│       ├── DeliveryManifest.java              # Dispatch manifest
│       ├── LogisticsProvider.java             # Dispatch gateway interface
│       ├── Order.java                         # Logistics bounded context order view
│       ├── OrderFulfillmentService.java       # Logistics domain service
│       ├── Warehouse.java                     # Inventory fulfillment aggregate
│       └── WarehouseRepository.java           # Warehouse repository
├── sales/
│   ├── application/
│   │   └── OrderCheckoutService.java          # Application service orchestrating checkout & events
│   └── domain/
│       ├── Order.java                         # Sales aggregate root
│       ├── OrderConfirmedEvent.java           # Decoupled domain event
│       ├── OrderRepository.java               # Sales repository
│       └── PaymentStatus.java                 # Payment status enumeration
└── store/
    └── domain/
        ├── Address.java                       # Value object
        ├── Customer.java                      # Customer aggregate root
        ├── Order.java                         # Small aggregate referencing customer by ID
        └── OrderItem.java                     # Line item value object
```

---

## Prerequisites

- **Java**: JDK 25
- **Maven**: 3.9+ (or use the bundled `./mvnw`)
- **Docker**: Optional (H2 in-memory profile runs unit and slice tests out of the box)

---

## Build & Test Suite

Execute the complete 21-test verification suite (unit tests, MockMvc web slices, and ArchUnit rules):

```bash
mvn clean test
```

### Expected Output

```text
[INFO] Results:
[INFO] 
[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## Running the Application

```bash
./mvnw spring-boot:run
```

Once running, query an order via the REST API:

```bash
curl -X GET http://localhost:8080/api/orders/00000000-0000-0000-0000-000000000001
```

---

## CI/CD Pipeline

This project is registered in the root GitHub Actions workflow:
- Path: `workshop/.github/workflows/build.yml`
- Job Matrix Entry: `architecture/ddd-mistakes-beginners-how-to-avoid`
