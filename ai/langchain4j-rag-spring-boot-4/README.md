# Building Advanced RAG Applications with LangChain4j and Spring Boot 4

A production-grade reference workshop project demonstrating how to build, test, and containerize **Retrieval-Augmented Generation (RAG)** systems using **Spring Boot 4**, **LangChain4j 0.35.0**, **PostgreSQL 16 with PgVector**, and **Java 25**.

Companion repository for the article: [Building Advanced RAG Applications with LangChain4j and Spring Boot 4](https://learn.webcodein.com/post/langchain4j-rag-spring-boot-4).

---

## Technical Stack
- **Language:** Java 25 (JDK)
- **Framework:** Spring Boot 4.0.0
- **AI Orchestration:** LangChain4j 0.35.0 (`langchain4j-spring-boot-starter`, `langchain4j-open-ai-spring-boot-starter`, `langchain4j-pgvector`)
- **Document Ingestion:** Apache PDFBox and LangChain4j Document Loaders
- **Vector Database:** PostgreSQL 16 with `pgvector` extension
- **Architecture Guardrails:** ArchUnit 1.4.1 (`archunit-junit5`)
- **Integration Testing:** Testcontainers 1.20.2 (`pgvector/pgvector:pg16`, `MockMvc`)
- **Unit Testing:** JUnit 5, Mockito, AssertJ

---

## Architecture Topology

```
                       +-----------------------------+
                       |    Raw Enterprise Documents |
                       | (PDF, Markdown, HTML, Text) |
                       +--------------+--------------+
                                      |
                                      v
                       +-----------------------------+
                       |   DocumentIngestionService  |
                       | - FileSystemDocumentLoader  |
                       | - DocumentSplitters (300/30)|
                       +--------------+--------------+
                                      |
                                      v
                       +-----------------------------+
                       |    PostgreSQL 16 (PgVector) |
                       | - EmbeddingStore<TextSegment|
                       | - 1536-dim OpenAI Embeddings|
                       +--------------+--------------+
                                      ^
                                      | Top-K Vector Search
                                      |
+-------------------+   HTTP GET      v       Augmented Context +-------------------+
| Enterprise User   | ------------> [ChatController] ---------> |  OpenAI Chat Model|
|  (Client Browser) | <------------ [EnterpriseCustomerSupport] |  (gpt-4o RAG)     |
+-------------------+   JSON Answer     (@AiService Proxy)      +-------------------+
```

---

## Project Structure

```
ai/langchain4j-rag-spring-boot-4/
├── pom.xml
├── README.md
├── compose.yaml
├── Dockerfile
└── src
    ├── main
    │   ├── java/com/webcodein/workshop/ai/rag/
    │   │   ├── RagApplication.java
    │   │   ├── ChatController.java
    │   │   ├── DocumentIngestionService.java
    │   │   └── EnterpriseCustomerSupport.java
    │   └── resources/
    │       └── application.properties
    └── test
        └── java/com/webcodein/workshop/ai/rag/
            ├── RagApplicationTests.java         (Integration tests with Testcontainers PgVector)
            └── Langchain4jArchitectureTest.java (5 ArchUnit architectural guardrails)
```

---

## Architectural Guardrails (ArchUnit)

This workshop implements automated architecture tests using ArchUnit 1.4.1 to ensure architectural conventions are strictly adhered to:
1. **Controller Naming & Annotations:** All classes ending in `Controller` must be annotated with `@RestController`.
2. **Service Stereotypes:** All classes ending in `Service` must be annotated with `@Service`.
3. **Declarative AI Services:** All declarative LangChain4j service interfaces ending with `CustomerSupport` must be interfaces annotated with `@AiService`.
4. **Decoupled Layers:** The service layer must never depend on or import classes from the controller layer.
5. **Controller Dependencies:** Controllers must only access approved domain classes, Spring framework components, and LangChain4j interfaces.

---

## Building and Running Tests

### Prerequisites
- JDK 25 installed and configured on your `PATH`.
- Docker Desktop or a compatible container runtime running for Testcontainers.

### Running All Tests
```bash
mvn clean test
```

### Running Architecture Tests
```bash
mvn test -Dtest=Langchain4jArchitectureTest
```

### Running Integration Tests with PgVector Testcontainer
```bash
mvn test -Dtest=RagApplicationTests
```
