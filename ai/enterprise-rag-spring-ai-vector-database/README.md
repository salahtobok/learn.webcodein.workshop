# Enterprise RAG Systems with Spring AI and PgVector

A production-grade reference workshop project demonstrating how to build and test secure, high-scale **Retrieval-Augmented Generation (RAG)** systems using **Spring Boot 4**, **Spring AI 2.0**, **PostgreSQL 16 with PgVector**, and **Java 25**.

Companion repository for the article: [Building Enterprise RAG Systems: Integrating Spring AI and Vector Databases in Java](https://learn.webcodein.com/post/enterprise-rag-spring-ai-vector-database).

---

## Technical Stack
- **Language:** Java 25 (JDK)
- **Framework:** Spring Boot 4.0.0
- **AI Orchestration:** Spring AI 2.0.1 (`spring-ai-starter-model-openai`, `spring-ai-starter-vector-store-pgvector`, `spring-ai-rag`)
- **Vector Database:** PostgreSQL 16 with `pgvector` extension
- **Architecture Testing:** ArchUnit 1.4.1 (`archunit-junit5`)
- **Integration Testing:** Testcontainers 1.20.1 (`@ServiceConnection`, `pgvector/pgvector:pg16`)
- **Unit Testing:** JUnit 5, Mockito, AssertJ

---

## Architectural Architecture

```
                       +-----------------------------+
                       |    Raw Enterprise Data      |
                       | (PDF, Markdown, HTML, Text) |
                       +--------------+--------------+
                                      |
                                      v
                       +-----------------------------+
                       |   DocumentIngestionService  |
                       | - TextReader                |
                       | - TokenTextSplitter (800 t) |
                       +--------------+--------------+
                                      |
                                      v
                       +-----------------------------+
                       |     PgVector VectorStore    |
                       | - High-dim embedding vectors|
                       | - HNSW / IVFFlat indexing   |
                       +--------------+--------------+
                                      ^
                                      | Top-K Cosine Search
                                      |
+-------------------+   HTTP GET      v       Prompt Enriched   +-------------------+
|  Enterprise Client| ------------> [RagController] ----------> |     OpenAI LLM    |
|   (Browser / API) | <------------ [ChatService]   <---------- |  (gpt-4o / RAG)   |
+-------------------+   JSON Answer (Augmentation Advisor)      +-------------------+
```

---

## Project Structure

```
ai/enterprise-rag-spring-ai-vector-database/
├── pom.xml
├── README.md
├── docker-compose.yml
└── src
    ├── main
    │   ├── java/com/webcodein/rag/
    │   │   ├── EnterpriseRagApplication.java
    │   │   ├── controller/
    │   │   │   └── RagController.java
    │   │   └── service/
    │   │       ├── ChatService.java
    │   │       └── DocumentIngestionService.java
    │   └── resources/
    │       └── application.yml
    └── test
        └── java/com/webcodein/rag/
            ├── EnterpriseRagApplicationTests.java  (Full Spring Context & Testcontainers PgVector)
            ├── RagControllerTest.java              (Web REST delegation unit tests)
            ├── DocumentIngestionServiceTest.java   (Token splitting and vector store ingestion)
            └── RagArchitectureTest.java            (5 ArchUnit architectural integrity guardrails)
```

---

## Architectural Guardrails (ArchUnit)

This workshop implements automated architecture tests using ArchUnit 1.4.1 to ensure architectural conventions are strictly followed:
1. **Controller Naming & Annotations:** All classes ending in `Controller` must be annotated with `@RestController`.
2. **Controller Packaging:** All controllers must reside exclusively in the `com.webcodein.rag.controller` package.
3. **Service Stereotypes:** All classes ending in `Service` must be annotated with `@Service`.
4. **Service Packaging:** All services must reside exclusively in the `com.webcodein.rag.service` package.
5. **Decoupling Layers:** The service layer must never depend on or import classes from the controller layer.

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
mvn test -Dtest=RagArchitectureTest
```

### Running Integration Tests with PgVector Testcontainer
```bash
mvn test -Dtest=EnterpriseRagApplicationTests
```
