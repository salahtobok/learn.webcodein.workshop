# jenkins-pipeline-spring-boot-4-graalvm

## Running Guide
```bash
./mvnw clean package
docker build -t app .
docker run -p 8080:8080 app
```
