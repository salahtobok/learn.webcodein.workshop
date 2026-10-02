# hexagonal-vs-clean-vs-onion-architecture

## Running Guide
```bash
./mvnw clean package
docker build -t app .
docker run -p 8080:8080 app
```
