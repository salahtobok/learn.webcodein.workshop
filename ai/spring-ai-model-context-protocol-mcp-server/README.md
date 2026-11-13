# Spring AI Model Context Protocol (MCP) Server

This project demonstrates an enterprise **Model Context Protocol (MCP)** server built with **Java 25**, **Spring Boot 4.0.0**, and **Spring AI 2.0**.

## Key Architectural Capabilities
1. **MCP Tool Registration**: Exposing enterprise service logic via `@Tool` annotations with schema definitions.
2. **Resource Providers**: Exposing enterprise product catalogs and business guidelines via `mcp://` URIs.
3. **Prompt Templates**: Structured prompt definitions for LLM interactions.
4. **ArchUnit Governance**: Strict architectural fitness rules enforcing controller, service, and tool boundaries.

## Tech Stack
- Java 25
- Spring Boot 4.0.0
- Spring AI 2.0.1
- ArchUnit 1.4.1
- JUnit 5 & Mockito

## Build & Test Instructions
```bash
# Run all unit, WebMvc slice, and ArchUnit fitness tests
mvn -B -ntp clean test
```

## Running the Application
```bash
mvn spring-boot:run
```

Access discovery endpoints:
- `GET http://localhost:8080/api/mcp/health`
- `GET http://localhost:8080/api/mcp/tools`
- `GET http://localhost:8080/api/mcp/resources`
- `GET http://localhost:8080/api/mcp/prompts`
