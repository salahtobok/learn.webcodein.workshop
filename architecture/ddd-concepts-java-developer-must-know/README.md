# 10 Essential DDD Concepts Every Java Developer Must Know

Companion source code for the in-depth technical article on [learn.webcodein.com](https://learn.webcodein.com/post/ddd-concepts-java-developer-must-know).

This project implements a clean Java 25 and Spring Boot 4.0.0 Bookstore domain model demonstrating all 10 foundational Domain-Driven Design (DDD) concepts in compilable, production-ready code.

---

## 10 Core DDD Concepts Implemented

| # | DDD Pattern | Implementation Class | Role & Architectural Boundary |
|---|---|---|---|
| 1 | **Ubiquitous Language** | `BookstoreMember`, `Order` | Real-world business vocabulary shared between domain experts and software engineers |
| 2 | **Bounded Context** | `sales.domain.Book` vs `shipping.domain.Book` | Explicit domain boundaries isolating independent models and ubiquitous languages |
| 3 | **Entity** | `Order` | Domain object identified by an immutable unique ID (`UUID`) rather than mutable attributes |
| 4 | **Value Object** | `Money`, `MemberId`, `ShippingDetails`, `Dimensions` | Immutable records defined purely by attribute values with rich validation invariants |
| 5 | **Aggregate Root** | `BookAggregate` (protecting `Chapter`) | Consistency boundary enforcing transaction invariants; internal entities cannot be mutated directly from outside |
| 6 | **Repository** | `BookRepository`, `InMemoryBookRepository` | Collection-oriented gateway mediating between the pure domain aggregate and persistence layers |
| 7 | **Domain Event** | `BookPublishedEvent`, `NotificationHandler` | Past-tense record of a business-critical occurrence published via Spring application events |
| 8 | **Domain Service** | `PricingService` | Stateless domain logic coordinator calculating multi-aggregate policies and dynamic member discounts |
| 9 | **Anti-Corruption Layer** | `LegacyShippingAcl`, `MockLegacySoapShippingClient` | Defensive translation layer converting unstructured legacy SOAP responses into domain records |
| 10 | **Specification Pattern** | `Specification`, `IsBestsellerSpec`, `IsInStockSpec`, `PromotionService` | Encapsulated, composable business rules chained dynamically with boolean logic (`and`, `or`, `not`) |

---

## Architecture & Package Structure

```text
com.bookstore
├── application
│   └── BookApplicationService.java       # Application orchestrator managing transactions
├── domain
│   ├── BookAggregate.java                 # Aggregate root enforcing internal invariants
│   ├── BookPublishedEvent.java            # Immutable domain event record
│   ├── BookRepository.java                # Repository interface for aggregate persistence
│   ├── BookstoreMember.java               # Member entity practicing ubiquitous language
│   ├── Chapter.java                       # Internal package-private entity
│   ├── IsBestsellerSpec.java              # Bestseller specification rule
│   ├── IsInStockSpec.java                 # Stock availability specification rule
│   ├── MemberId.java                      # Value object record for member identity
│   ├── Money.java                         # Value object record with arithmetic invariants
│   ├── Order.java                         # Order entity with identity-based equality
│   ├── PricingService.java                # Stateless domain service for pricing policies
│   ├── PromotionService.java              # Promotion service combining specifications
│   ├── ShippingDetails.java               # Value object record for logistics data
│   └── Specification.java                 # Generic composable specification interface
├── infrastructure
│   ├── InMemoryBookRepository.java        # In-memory repository adapter
│   ├── LegacyShippingAcl.java             # Anti-corruption layer translating legacy DTOs
│   ├── LegacyShippingResponse.java        # Raw legacy response object
│   ├── LegacySoapShippingClient.java      # Legacy interface contract
│   └── MockLegacySoapShippingClient.java  # Mock client implementation
├── sales
│   └── domain
│       └── Book.java                      # Sales context book representation
├── shipping
│   └── domain
│       ├── Book.java                      # Shipping context book representation
│       └── Dimensions.java                # Value object record for spatial geometry
└── BookstoreApplication.java             # Spring Boot 4 entry point
```

---

## Prerequisites

- **Java 25** (e.g. Amazon Corretto 25, Eclipse Temurin 25)
- **Maven 3.9+** (or use the included `./mvnw` wrapper)
- **Docker Desktop** (optional, for container execution)

---

## Build & Test Verification

Run the full build and automated test suite:

```bash
# Unix / macOS
./mvnw clean verify

# Windows PowerShell
.\mvnw.cmd clean verify
```

The automated test suite runs 32 tests covering:
- Invariant enforcement on `Money` value objects (preventing negative values and currency mismatches)
- Entity equality semantics on `Order` (identity-based equality regardless of lifecycle mutations)
- Aggregate boundary protection on `BookAggregate` (chapter title guards, encapsulation)
- Dynamic rule composition on `Specification` (`IsBestsellerSpec.and(IsInStockSpec)`)
- Pricing policies on `PricingService` (VIP discounts, clearance flat deductions)
- Anti-Corruption Layer translation on `LegacyShippingAcl`
- Context boundary independence between `sales.Book` and `shipping.Book`
- ArchUnit architectural governance rules enforcing DDD layer boundaries (`DddArchitectureTest`)
- Spring Boot 4 ApplicationContext health check (`BookstoreApplicationTests`)

---

## Architectural Governance with ArchUnit

Compile-time architectural rules ensure that developers do not degrade the domain model:

1. **Domain Isolation**: `domain` packages must never depend on `application` or `infrastructure`.
2. **Domain Services**: All services in `domain` must be annotated with `@Service`.
3. **Repository Contracts**: Repositories in `domain` must always be interfaces.

---

## Running with Docker

Build and run the containerized image:

```bash
docker build -t ddd-concepts-java-developer-must-know .
docker run --rm -p 8080:8080 ddd-concepts-java-developer-must-know
```

---

## Related Guides & Resources

- [Domain-Driven Design (DDD) Masterclass: The Ultimate Java Developer Guide](https://learn.webcodein.com/post/ddd-masterclass-java-guide)
- [DDD vs CRUD: Visual Guide for Java Developers](https://learn.webcodein.com/post/ddd-vs-crud-visual-guide-java-developers)
- [Domain-Driven Design Explained Simply: A Beginner's Complete Guide](https://learn.webcodein.com/post/domain-driven-design-explained-beginners-guide)
- [Bounded Context in DDD Explained](https://learn.webcodein.com/post/bounded-context-ddd-explained-beginners)
- [DDD Layers Explained: Where Does Each Piece of Code Go?](https://learn.webcodein.com/post/ddd-layers-explained-code-structure-spring-boot)
- [Hexagonal Architecture vs Clean Architecture vs Onion Architecture](https://learn.webcodein.com/post/hexagonal-vs-clean-vs-onion-architecture)
