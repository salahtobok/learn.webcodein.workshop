# Building AI Agents with Spring AI 2.0 and Function Calling

Companion workshop repository for the tutorial: [**Building AI Agents with Spring AI 2.0 and Function Calling**](https://learn.webcodein.com/post/spring-ai-2-0-function-calling-agents).

This reference project demonstrates how to build autonomous, deterministic enterprise AI agents in **Spring Boot 4.0.0** and **Java 25** using **Spring AI 2.0.1** and the `@Tool` declaration model.

---

## Architectural Highlights

1. **Reflection-Driven Schema Synthesis (`@Tool`)**:
   - Converts standard Spring service methods into OpenAPI-compliant JSON Schemas dispatched to LLMs automatically.
2. **Deterministic Java 25 Domain Model**:
   - Uses immutable Java 25 records for tool inputs and outputs (`OrderStatusRequest`, `CancelOrderRequest`), enforcing compile-time validation.
3. **Multi-Turn Agent Coordination (`CustomerSupportAgent`)**:
   - Coordinates dynamic tool selection, parameter extraction, and natural language synthesis using the fluent `ChatClient` builder.
4. **Architectural Guardrails (`ArchUnit 1.4.1`)**:
   - Automated architecture rules enforcing controller, service, and tool package encapsulation and annotations.

---

## Tech Stack

- **Java**: 25 LTS
- **Framework**: Spring Boot 4.0.0, Spring AI 2.0.1
- **Testing**: JUnit 5, AssertJ, Spring Boot Test, MockMvc, ArchUnit 1.4.1
- **Build Tool**: Maven 3.9+

---

## Prerequisites

- JDK 25 or newer
- Maven 3.9+ (or use the included `./mvnw` wrapper)
- Docker & Docker Compose (optional)
- OpenAI API Key (`export OPENAI_API_KEY=<SECRET>`) for live LLM requests

---

## Building and Testing

### Run All Tests (Integration + Architectural)
```bash
./mvnw clean test
```

### Full Verification Build
```bash
./mvnw clean verify
```

All 11 automated tests pass 100%:
- **1 Integration Test (`ChatControllerIntegrationTest`)**:
  - Full Spring Boot context startup, Actuator endpoints, and MockMvc dispatch.
- **5 Tool Unit Tests (`OrderToolsTest`)**:
  - Valid order status retrieval (`ORD-1001`).
  - Graceful handling of missing orders (`ORD-9999`).
  - Order cancellation validation and status updating.
  - Rejection of cancellation for already-shipped orders.
- **5 Architectural Tests (`AgentArchitectureTest`)**:
  - Controllers carry `@RestController` and reside in `..controller..`.
  - Agents carry `@Service` and reside in `..service..`.
  - Tool classes carry `@Component` and reside in `..tool..`.

---

## Running Locally

```bash
export OPENAI_API_KEY="<SECRET>"
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

---

## Example API Requests

### 1. Order Status Query (Autonomous Tool Dispatch)
```bash
curl -X POST http://localhost:8080/api/support/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "Can you check the status of order ORD-1001?"}'
```

**Agent Response:**
```json
{
  "response": "Order ORD-1001 for Salah Tobok is currently SHIPPED. It contains 1x Enterprise SSD and was shipped on 2026-10-24."
}
```

### 2. Order Cancellation Attempt
```bash
curl -X POST http://localhost:8080/api/support/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "Please cancel order ORD-1002, I ordered the wrong item."}'
```

---

## Running via Docker Compose

```bash
export OPENAI_API_KEY="<SECRET>"
docker compose up --build
```

---

## License

This project is part of the `learn.webcodein.com` companion workshop code repository and is open-sourced under the MIT License.
