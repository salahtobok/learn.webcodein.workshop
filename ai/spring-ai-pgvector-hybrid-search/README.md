# Spring AI & PostgreSQL pgvector: Hybrid Search (BM25 + Dense Vectors) in Spring Boot 4

Companion code repository for the article: [Spring AI & PostgreSQL pgvector: Implementing Hybrid Search (BM25 + Dense Vectors) in Spring Boot 4](https://learn.webcodein.com/post/spring-ai-pgvector-hybrid-search).

---

## 1. Architectural Overview

This project demonstrates an enterprise **Hybrid Search Engine** combining **PostgreSQL 16 pgvector** dense vector embeddings with **sparse BM25 full-text search** in **Spring AI** and **Spring Boot 4.0.0** on **Java 25**.

### Key Architectural Concepts:
- **Overcoming Semantic Blindness:** Pure dense vector search fails on exact product SKUs, part numbers, and error codes. Sparse keyword search fails on semantic synonyms and concepts. Hybrid search merges both paradigms.
- **Reciprocal Rank Fusion (RRF):** Evaluates ranks from dense vector cosine similarity and full-text inverted indexes, combining them using the formula: $RRF(d) = \sum \frac{1}{k + r(d)}$.
- **HNSW & GIN Indexing:** Leverages PostgreSQL 16 HNSW vector graph indexing alongside GIN full-text `tsvector` indexes.
- **Automated Architectural Fitness (ArchUnit 1.4.1):** Enforces controller-service boundaries and immutable records.

---

## 2. Technology Stack

- **Java:** JDK 25
- **Framework:** Spring Boot 4.0.0
- **AI Engine:** Spring AI 2.0.1
- **Testing:** JUnit 5, AssertJ, Spring Boot Test (`MockMvc`), ArchUnit 1.4.1

---

## 3. Running & Verifying Locally

### Maven Verification
To compile the application and execute all unit, slice, and architectural fitness tests:

```bash
mvn clean test
```

### Running the Application
```bash
mvn spring-boot:run
```

---

## 4. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/search/hybrid?query=...` | Executes Reciprocal Rank Fusion hybrid search. |
| `POST` | `/api/search/dense` | Performs dense vector similarity search. |
| `GET` | `/api/search/keyword?query=...` | Performs sparse full-text keyword search. |
| `POST` | `/api/search/documents` | Indexes a new document with title, text, and vector embedding. |
| `GET` | `/api/search/stats` | Returns total indexed document count and search mode. |
