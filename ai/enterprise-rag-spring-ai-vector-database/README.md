# enterprise-rag-spring-ai-vector-database

## Running Guide
```bash
./mvnw clean package
docker build -t app .
docker run -p 8080:8080 app
```
