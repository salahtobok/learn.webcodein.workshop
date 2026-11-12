# Enforcing Hexagonal Architecture with ArchUnit in Spring Boot 4

This project demonstrates how to enforce strict Hexagonal Architecture (Ports and Adapters) boundaries using ArchUnit 1.4.1 in Spring Boot 4 on Java 25.

Companion Article: [Enforcing Hexagonal Architecture with ArchUnit in Spring Boot 4](https://learn.webcodein.com/post/archunit-hexagonal-architecture-spring-boot)

---

## Architectural Boundaries

Hexagonal Architecture isolates business logic from external frameworks, user interfaces, and infrastructure adapters:

```
[ Inbound Adapters ] ---> [ Inbound Ports (Use Cases) ] ---> [ Domain Core ]
(REST Controllers)                                                  ^
                                                                     |
[ Outbound Adapters ] <--- [ Outbound Ports (SPI) ] -----------------+
(JPA Repositories)
```

1. **Domain (`domain`)**:
   - Contains pure business entities and invariants (`Order`).
   - Zero dependencies on Spring, Jakarta Persistence, or external libraries.
2. **Application Ports (`application.port.in` & `application.port.out`)**:
   - Inbound ports define use cases (`PlaceOrderUseCase`, `GetOrderUseCase`).
   - Outbound ports define secondary SPI contracts (`OrderRepositoryPort`).
3. **Application Services (`application.service`)**:
   - Coordinates business workflows (`OrderService`) by implementing inbound ports and calling outbound ports.
4. **Adapters (`adapter.in` & `adapter.out`)**:
   - Primary/Inbound: `OrderController` translates HTTP requests into use case calls.
   - Secondary/Outbound: `OrderPersistenceAdapter` implements `OrderRepositoryPort` using `OrderJpaRepository`.

---

## ArchUnit Governance Rules

All architectural rules are verified during the standard Maven build:

1. **Domain Purity**: Domain classes never depend on application, adapter, Spring, or JPA packages.
2. **Adapter Isolation**: Inbound adapters never depend directly on outbound adapters.
3. **Application Decoupling**: Application services only depend on ports, domain models, and standard Java libraries.
4. **Port Interface Enforcement**: All ports in `application.port..` must be Java interfaces.
5. **Onion/Hexagonal Architecture Enforcement**: Built-in `Architectures.onionArchitecture()` validates all architectural layers.
6. **Generic Exception Prohibition**: No classes may throw raw `Exception`, `RuntimeException`, or `Throwable`.
7. **Field Injection Prohibition**: Direct field injection (`@Autowired` on private fields) is forbidden; constructor injection is enforced.

---

## Tech Stack
- **Java**: 25 (LTS)
- **Framework**: Spring Boot 4.0.0 GA
- **Architecture Verification**: ArchUnit 1.4.1
- **Persistence**: Spring Data JPA & H2 In-Memory Database
- **Testing**: JUnit 5, Mockito, Spring Boot Test / MockMvc

---

## Verification & Execution

### Run All Unit, Slice, and Architecture Tests
```bash
./mvnw clean test
```

### Run Application Locally
```bash
./mvnw spring-boot:run
```

### Test API via cURL
Place an order:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"product": "Hexagonal Architecture with Spring Boot", "quantity": 1}'
```

Retrieve an order:
```bash
curl -X GET http://localhost:8080/api/orders/{id}
```
