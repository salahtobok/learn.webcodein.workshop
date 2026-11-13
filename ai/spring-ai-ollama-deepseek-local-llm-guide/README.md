# Local GenAI with Spring AI, Ollama, and DeepSeek

This companion project demonstrates **Privacy-First, Offline Generative AI** in enterprise backend systems using **Spring Boot 4.0.0**, **Java 25**, and **Spring AI with Ollama**.

## Key Capabilities
- **Local LLM Execution**: Runs inference against local Ollama models (e.g., `deepseek-r1:8b`, `llama3.2`, `mistral`) with zero external cloud API dependencies.
- **Enterprise Data Privacy**: Local document compliance auditing preventing sensitive data egress.
- **ArchUnit Rules**: Architecture verification enforcing strict separation between controller and service layers.

## Tech Stack
- Java 25
- Spring Boot 4.0.0
- Spring AI 2.0.1 (Ollama starter)
- ArchUnit 1.4.1
- JUnit 5 & Mockito

## Testing
```bash
mvn -B -ntp clean test
```
