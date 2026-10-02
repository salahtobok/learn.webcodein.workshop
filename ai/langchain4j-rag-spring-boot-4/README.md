# WebCodein Workshop: LangChain4j RAG with Spring Boot 4

This is the companion code for the article **Building Advanced RAG Applications with LangChain4j and Spring Boot 4**.

## Prerequisites
- Java 25
- Docker and Docker Compose (for PostgreSQL PgVector)
- OpenAI API Key

## Running Guide

1. **Start the Vector Database:**
   ```bash
   docker compose up -d
   ```

2. **Configure API Key:**
   Export your OpenAI API Key as an environment variable:
   ```bash
   # On Linux/macOS
   export OPENAI_API_KEY=your_key_here
   
   # On Windows (PowerShell)
   $env:OPENAI_API_KEY="your_key_here"
   ```

3. **Build the Project:**
   ```bash
   ./mvnw clean verify
   ```
   *(Note: This runs the Testcontainers integration tests automatically).*

4. **Run the Application Locally:**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Test the Endpoint:**
   ```bash
   curl "http://localhost:8080/api/chat?query=What%20is%20LangChain4j"
   ```

## Docker Containerization
To run the entire application inside Docker:
```bash
docker build -t langchain4j-rag-demo .
docker run -p 8080:8080 -e OPENAI_API_KEY=$OPENAI_API_KEY langchain4j-rag-demo
```
