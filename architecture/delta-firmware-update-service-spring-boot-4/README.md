# Delta Firmware Update Service (Spring Boot 4)

This is the companion code for the WebCodein tutorial on building a Delta Firmware Update server with Spring Boot 4.

## Prerequisites
- Java 25
- Docker (for Testcontainers)

## Running Locally

Build the project using the included Maven wrapper:
```bash
./mvnw clean package
```

Run the tests (which use Testcontainers to spin up PostgreSQL automatically):
```bash
./mvnw test
```

## Running via Docker
You can build the Docker image and run it:
```bash
docker build -t delta-firmware-update-service .
docker run -p 8080:8080 delta-firmware-update-service
```
