# Jenkins Declarative Pipeline with Testcontainers & GraalVM Native Image (Spring Boot 4)

This companion workshop project demonstrates an enterprise-grade CI/CD pipeline implementation using **Jenkins Declarative Pipelines**, ephemeral **Docker agent pods**, **Testcontainers 2.0.2** sibling containers, and **GraalVM Native Image AOT compilation** on **Java 25** and **Spring Boot 4.0.0**.

---

## Architecture Highlights

1. **Declarative Jenkins Pipeline (`Jenkinsfile`)**:
   - Containerized pipeline execution within GraalVM JDK 25 runner environments.
   - Sibling Docker container orchestration via host socket mounting (`/var/run/docker.sock`).
   - Automated test reporting, surefire result extraction, and fail-safe artifact packaging.
2. **Dynamic Integration Testing**:
   - Automated integration tests powered by Testcontainers 2.x and JUnit 5.
   - Ephemeral container spin-up and teardown isolated per stage.
3. **ArchUnit 1.4.1 Architectural Rules**:
   - Architectural boundary enforcement ensuring controllers and services obey layering constraints.
   - Prohibits legacy temporal APIs (`java.util.Date`, `java.util.Calendar`) and enforces `java.time`.
   - Prevents field injection and generic exceptions.
4. **GraalVM Native Compilation**:
   - Ahead-Of-Time (AOT) compilation producing self-contained native Linux/Windows binaries with sub-20ms cold start and minimal RSS memory footprint.

---

## Project Structure

```text
jenkins-pipeline-spring-boot-4-graalvm/
├── Dockerfile                  # Lightweight container packaging
├── Jenkinsfile                 # Declarative pipeline script with Docker socket mount
├── pom.xml                     # Maven project configuration (Boot 4.0.0, Java 25, Testcontainers)
├── README.md                   # Project documentation and quickstart
└── src/
    ├── main/java/com/webcodein/jenkins/
    │   ├── JenkinsApplication.java             # Spring Boot 4 entry point
    │   ├── controller/
    │   │   └── StatusController.java          # REST status probe controller
    │   ├── model/
    │   │   └── BuildPipelineStatus.java       # Immutable Java 25 record
    │   └── service/
    │       └── PipelineStatusService.java     # Pipeline health service
    └── test/java/com/webcodein/jenkins/
        ├── DockerPipelineIntegrationTest.java  # Sibling container integration test
        ├── JenkinsApplicationTests.java        # Spring context boot test
        ├── JenkinsArchitectureTest.java       # ArchUnit rule verification suite
        ├── controller/
        │   └── StatusControllerTest.java      # MockMvc slice test
        └── service/
            └── PipelineStatusServiceTest.java # Service unit test
```

---

## Build, Test & Run

### 1. Run Unit, Slice & Integration Tests
```bash
mvn clean test
```

### 2. Package Executable JAR
```bash
mvn clean package -DskipTests
java -jar target/jenkins-pipeline-spring-boot-4-graalvm-0.0.1-SNAPSHOT.jar
```

### 3. Verify Health Endpoint
```bash
curl -X GET http://localhost:8080/api/status
```

Expected JSON response:
```json
{
  "application": "jenkins-pipeline-spring-boot-4-graalvm",
  "status": "UP",
  "pipelineEngine": "Jenkins Declarative Pipeline",
  "jvmRuntime": "25+36-LTS",
  "graalvmNativeTarget": "Linux x86_64 AOT Native Executable",
  "checkedAt": "2026-11-12T03:32:00Z"
}
```

### 4. Build GraalVM Native Image
```bash
mvn -Pnative native:compile
./target/jenkins-pipeline-spring-boot-4-graalvm
```

### 5. Build Container Image via Spring Boot Buildpack
```bash
mvn spring-boot:build-image
docker run -p 8080:8080 jenkins-pipeline-spring-boot-4-graalvm:0.0.1-SNAPSHOT
```
