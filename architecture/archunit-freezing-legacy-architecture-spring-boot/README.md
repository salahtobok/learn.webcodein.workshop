# ArchUnit Freezing Legacy Architecture in Spring Boot

This is the companion code for the Webcodein article on freezing legacy architecture with ArchUnit.

## Overview
This project demonstrates how a "Big Ball of Mud" legacy architecture can be safely frozen using ArchUnit's `FreezingArchRule`. This allows the team to stop introducing new architectural violations while gradually refactoring existing ones.

## Requirements
- Java 25
- Maven 3.9+
- Docker (optional)

## Building the Project
Run the following command to build the project and execute the ArchUnit tests:

```bash
./mvnw clean verify
```

Notice that during the first run, the build will pass and ArchUnit will create the violation store in `src/test/resources/archunit-violations`.

If you add a new violation to the codebase and run `./mvnw clean verify` again, the build will fail.

## Running the Application
To run the Spring Boot application locally:
```bash
./mvnw spring-boot:run
```

## Running via Docker
If you want to package the application as a Docker image using Cloud Native Buildpacks (GraalVM/JVM):
```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=webcodein/legacy-freezing
docker run -p 8080:8080 webcodein/legacy-freezing
```
