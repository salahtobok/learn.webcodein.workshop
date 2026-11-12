# Ubiquitous Language in DDD: How to Name Things Right

Companion code repository for the enterprise architectural guide:  
**"Ubiquitous Language in DDD: How to Name Things Right | Guide"**  
Article: [https://learn.webcodein.com/post/ubiquitous-language-ddd-naming-things](https://learn.webcodein.com/post/ubiquitous-language-ddd-naming-things)

---

## Technical Stack
- **Java 25** (JDK records, compact constructor validation, pattern matching)
- **Spring Boot 4.0.0 GA** (Spring MVC, Spring Data JPA)
- **ArchUnit 1.4.1** (Automated governance rules enforcing ubiquitous domain naming)
- **H2 / PostgreSQL** (In-memory testing and production persistence)
- **JUnit 5 & Mockito** (Comprehensive test suite)

---

## Domain Overview: Library Management System
This workshop demonstrates how to eliminate the semantic translation gap between business experts and software developers by modeling a library domain with pure Ubiquitous Language:

- **Aggregates & Entities:**
  - `Book`: Aggregate with intention-revealing methods (`markBookAsLost()`, `correctBookTitle(String newTitle)`).
  - `Patron`: Aggregate Root modeling a library member with domain lifecycle methods (`borrow(Book book)`, `activateAccount()`, `suspendAccount()`, `payFine(Money amount)`).
  - `Loan`: Lifecycle entity tracking borrowed materials (`approve()`, `reject()`, due dates).
- **Value Objects:**
  - `Isbn`: Strongly-typed record encapsulating ISBN formatting and validation.
  - `Money`: Immutable record protecting financial non-negativity and ISO currency codes.
  - `PatronProfile`: Immutable record holding patron contact and library card details.
- **Domain Services:**
  - `LibraryCatalog`: Coordinates cataloging and registering new books.
  - `LoanService`: Orchestrates loan approval and rejection workflows.
  - `PatronService`: Domain service for charging overdue fines.
- **Presentation REST API:**
  - `PatronController`: REST endpoints (`POST /api/patrons`, `GET /api/patrons/{id}`, `POST /api/patrons/{id}/suspend`, `POST /api/patrons/{id}/activate`).

---

## Architectural Rules Enforced with ArchUnit
The project enforces automated ubiquitous language governance rules:
1. **No Vague Technical Suffixes:** Prohibits domain classes ending in `Manager`, `Helper`, `Processor`, `DataHolder`, or `InfoObject`.
2. **Domain Isolation:** The domain layer must never depend on the presentation layer.
3. **No Web Framework Leakage:** Domain classes remain pure Java and never import `org.springframework.web..` packages.

---

## Build and Test

Execute the full 28-test suite with Maven and JDK 25:
```bash
mvn -B -ntp clean test
```

### Test Coverage Summary (28 Tests)
- **`BookTest` (4 tests):** Validates book creation, unlost state, title correction, and blank title rejection.
- **`IsbnTest` (4 tests):** Validates ISBN creation, null/blank rejection, and record value equality.
- **`LoanTest` (3 tests):** Validates loan creation, approval, and rejection.
- **`MoneyTest` (4 tests):** Validates positive monetary creation, negative rejection, and value equality.
- **`PatronTest` (5 tests):** Validates borrowing rules, suspension, reactivation, and fine payment.
- **`PatronControllerTest` (4 tests):** MockMvc slice testing for registration, retrieval, suspension, and 404 handling.
- **`UbiquitousLanguageArchitectureTest` (4 tests):** ArchUnit rules enforcing domain naming conventions and layer boundaries.

---

## License
MIT. Free for education and enterprise reference.
