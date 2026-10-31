# Spring AI 2.0 with Spring Boot 4

Companion code repository for the complete guide on [learn.webcodein.com](https://learn.webcodein.com/post/spring-ai-2-0-spring-boot-4-complete-guide).

This production-grade reference project demonstrates how to build artificial intelligence capabilities into Java 25 enterprise applications using **Spring Boot 4.0.0** and **Spring AI 2.0.1**.

---

## Architectural Highlights

- **Fluent ChatClient API**: Builder-based client configuration with declarative system instructions, runtime parameter overrides, and fluent chaining.
- **Dynamic Advisor Chain**: Pluggable interceptor pipeline with `MessageChatMemoryAdvisor` for stateful conversation context management.
- **Tool Calling / Function Calling (`@Tool`)**: Method-based tool registration with reflection-based schema generation for autonomous agent execution (`WeatherService`).
- **Typed Structured Output**: Automatic JSON schema synthesis and mapping from unstructured LLM responses into immutable Java 25 records (`Author`).
- **Flexible Model Backends**: Out-of-the-box dual configuration for cloud LLMs (OpenAI `gpt-4o-mini`) and local containerized inference engines (Ollama `llama3`).

---

## Project Structure

```
ai/spring-ai-2-0-spring-boot-4/
├── compose.yaml                                          # Local Ollama llama3 container configuration
├── Dockerfile                                            # Multi-stage container build definition
├── mvnw / mvnw.cmd                                       # Maven 3.9+ wrapper (LF line endings)
├── pom.xml                                               # Java 25, Spring Boot 4.0.0, Spring AI 2.0.1
└── src/
    ├── main/
    │   ├── java/com/webcodein/ai/
    │   │   ├── AiController.java                         # REST controller (/api/chat, /api/weather, /api/author)
    │   │   ├── SpringAiApplication.java                  # Main entry point & ChatMemory bean configuration
    │   │   └── WeatherService.java                       # @Tool annotated service for function calling
    │   └── resources/
    │       └── application.properties                    # OpenAI and Ollama fallback properties
    └── test/
        └── java/com/webcodein/ai/
            ├── AiControllerTest.java                     # Mockito unit tests for fluent ChatClient endpoints (4 tests)
            ├── SpringAiApplicationTests.java             # Spring context & ChatMemory state integration (2 tests)
            └── WeatherServiceTest.java                   # Unit tests for @Tool reflection & Java 25 records (3 tests)
```

---

## Prerequisites

- **Java Development Kit (JDK)**: Version 25 (or 21+)
- **Apache Maven**: Version 3.9+ (or use the included `./mvnw`)
- **Docker / Docker Compose**: (Optional, for local Ollama execution)
- **OpenAI API Key**: (Optional, if testing live OpenAI cloud calls)

---

## Build and Verification

Compile the project and run the complete automated test suite (9 tests):

```bash
mvn clean verify
```

Expected output:
```text
[INFO] Running com.webcodein.ai.AiControllerTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.ai.SpringAiApplicationTests
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.webcodein.ai.WeatherServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Running the Application

### Option A: Using OpenAI (Cloud)

Provide your OpenAI API key as an environment variable and launch Spring Boot:

```bash
export OPENAI_API_KEY="sk-proj-your-key-here"
mvn spring-boot:run
```

### Option B: Using Ollama (Local Container)

Start the local Ollama instance with Docker Compose:

```bash
docker compose up -d
```

Once Ollama has downloaded `llama3`, run the application:

```bash
mvn spring-boot:run
```

---

## Testing the REST Endpoints

### 1. Conversational Chat with Memory (`GET /api/chat`)

Send chat messages retaining context across calls using the `chatId` query parameter:

```bash
curl -X GET "http://localhost:8080/api/chat?message=My%20name%20is%20Salah&chatId=session-1"
# Response: "Hello Salah! It is a pleasure to meet you. How can I assist you today?"

curl -X GET "http://localhost:8080/api/chat?message=What%20is%20my%20name?&chatId=session-1"
# Response: "Your name is Salah! You told me earlier in this session."
```

### 2. Autonomous Function Calling (`GET /api/weather`)

Invokes the `@Tool` annotated `WeatherService` to retrieve dynamic data:

```bash
curl -X GET "http://localhost:8080/api/weather?city=Paris"
# Response: "The current weather in Paris is Sunny with a temperature of 24°C."
```

### 3. Structured Output Mapping (`GET /api/author`)

Extracts typed Java 25 records (`Author`) from the model:

```bash
curl -X GET "http://localhost:8080/api/author?name=J.R.R.%20Tolkien"
```

Response:
```json
{
  "name": "J.R.R. Tolkien",
  "biography": "John Ronald Reuel Tolkien was an English writer, poet, philologist, and academic, best known as the author of classic high fantasy works.",
  "famousBooks": [
    "The Hobbit",
    "The Fellowship of the Ring",
    "The Two Towers",
    "The Return of the King",
    "The Silmarillion"
  ]
}
```

---

## Related Articles

- [Spring AI 2.0: The Complete Guide to AI Integration in Spring Boot 4](https://learn.webcodein.com/post/spring-ai-2-0-spring-boot-4-complete-guide)
- [Enterprise RAG with Spring AI and PgVector](https://learn.webcodein.com/post/enterprise-rag-spring-ai-vector-database)
- [LangChain4j RAG in Spring Boot 4](https://learn.webcodein.com/post/langchain4j-rag-spring-boot-4)
- [Clean Architecture with Spring Boot 4](https://learn.webcodein.com/post/clean-architecture-hexagonal-ports-adapters-spring-boot-4)
- [High-Concurrency APIs with Java 25 Virtual Threads](https://learn.webcodein.com/post/high-concurrency-apis-java-25-virtual-threads-spring-boot-4)
