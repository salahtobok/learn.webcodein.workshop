# Building AI Agents with Spring AI 2.0 and Function Calling

This is the companion code for the Webcodein article on building AI Agents with Spring AI 2.0 and Spring Boot 4.

## Prerequisites
- Java 25
- Maven 3.9+
- Docker & Docker Compose
- An OpenAI API Key (`OPENAI_API_KEY`)

## Running Locally

1. Export your API key:
   ```bash
   export OPENAI_API_KEY=your-actual-api-key
   ```
2. Build and run using the Maven wrapper:
   ```bash
   ./mvnw clean package
   ./mvnw spring-boot:run
   ```

## Running via Docker Compose

1. Export your API key:
   ```bash
   export OPENAI_API_KEY=your-actual-api-key
   ```
2. Build and run the containers:
   ```bash
   docker compose up --build
   ```

## Testing the API

You can test the agent using `curl`:

```bash
curl -X POST http://localhost:8080/api/support/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "Can you check the status of order ORD-1001?"}'
```

The agent will use Function Calling to invoke the `getOrderStatus` Java method, read the result, and respond natively to the user.
