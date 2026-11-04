# Bounded Context in Domain-Driven Design (DDD) Explained | Spring Boot 4 Workshop

Companion code project demonstrating **Bounded Contexts** and **Context Mapping** in a Spring Boot 4 modular monolith.

## Architecture Overview

This project implements two autonomous Bounded Contexts within a single Spring Boot application, isolated at the package and database table levels:

1. **Catalog Context (`com.webcodein.boundedcontext.catalog`)**:
   - Manages marketing product definitions (`sku`, `name`, `description`, `price`).
   - Serves as the **Upstream (U)** service exposing read-only DTOs (`ProductResponseDto`).
   - Persists data into the `catalog_products` table.

2. **Order Context (`com.webcodein.boundedcontext.order`)**:
   - Serves as the **Downstream (D)** consumer managing customer orders and purchases.
   - Embeds an **Anti-Corruption Layer (ACL)** (`CatalogAclAdapter`) that maps external Catalog DTOs into internal `ProductSnapshot` value objects and `OrderItem` entities.
   - Locks purchase price at order creation time to protect against catalog price changes.
   - Persists data into `orders` and `order_items` tables without JPA foreign keys to the catalog schema.

3. **Architecture Governance (`com.webcodein.boundedcontext.architecture`)**:
   - Automated compile-time fitness functions with **ArchUnit** ensuring:
     - The Upstream Catalog context never depends on the Downstream Order context.
     - The Order domain never references internal Catalog entities directly (only via the ACL).
     - Packages are strictly free of circular dependencies.

## Project Structure

```text
src/main/java/com/webcodein/boundedcontext/
├── EcommerceApplication.java
├── catalog/
│   ├── Product.java                  # Catalog Aggregate Entity
│   ├── ProductRepository.java        # Spring Data JPA Repository
│   ├── CatalogService.java           # Application Service
│   ├── CatalogController.java        # REST Controller (/api/catalog/products)
│   └── dto/
│       ├── CreateProductRequest.java
│       └── ProductResponseDto.java   # Published Language (PL) DTO
├── order/
│   ├── Order.java                    # Order Aggregate Root
│   ├── OrderItem.java                # Order Line Item Snapshot
│   ├── OrderRepository.java          # Spring Data JPA Repository
│   ├── OrderService.java             # Order Orchestration Service
│   ├── OrderController.java          # REST Controller (/api/orders)
│   ├── acl/
│   │   ├── CatalogClient.java        # Downstream Interface Contract
│   │   ├── ProductSnapshot.java      # Order Value Object
│   │   └── CatalogAclAdapter.java    # Anti-Corruption Layer Implementation
│   └── dto/
│       ├── CreateOrderRequest.java
│       ├── OrderItemRequest.java
│       ├── OrderItemResponseDto.java
│       └── OrderResponseDto.java
```

## Running the Application

### Prerequisites
- JDK 25+
- Maven 3.9+

### Run with Maven Wrapper
```bash
./mvnw spring-boot:run
```

## Running Tests

The test suite includes domain unit tests, WebMvc slice tests, and ArchUnit architecture tests:

```bash
./mvnw clean test
```

### Verified Test Suite (20 tests passed 100%):
- `CatalogControllerTest`: MockMvc slice tests for catalog endpoints.
- `ProductTest`: Unit tests for catalog invariant rules.
- `OrderControllerTest`: MockMvc slice tests for order placement and confirmation.
- `OrderTest`: Unit tests for order state transitions, line item calculation, and immutability invariants.
- `CatalogAclAdapterTest`: Unit tests for Anti-Corruption Layer mapping and defensive isolation.
- `BoundedContextArchitectureTest`: ArchUnit architecture fitness rules enforcing upstream/downstream boundaries.
- `EcommerceApplicationTests`: Spring Boot application context load verification.
