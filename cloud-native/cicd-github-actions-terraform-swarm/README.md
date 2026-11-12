# Cloud-Native CI/CD: Deploying Spring Boot 4 to Docker Swarm with GitHub Actions & Terraform

Companion reference implementation for the technical guide:
**[Cloud-Native CI/CD: Deploying Spring Boot 4 to Docker Swarm with GitHub Actions & Terraform](https://learn.webcodein.com/post/cicd-github-actions-terraform-swarm)**

This project demonstrates a production-grade cloud-native continuous integration and deployment (CI/CD) pipeline for Spring Boot 4 on Java 25:
- **GitHub Actions** for automated build, test, and container image publishing.
- **Terraform** for declarative cloud infrastructure provisioning.
- **Docker Swarm** with ingress routing mesh and Raft consensus for zero-downtime rolling service updates.
- **ArchUnit 1.4.1** for structural architectural governance.

---

## Technical Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java (OpenJDK) | 25 |
| **Framework** | Spring Boot | 4.0.0 |
| **Container Engine** | Docker & Docker Swarm | 24+ |
| **Infrastructure as Code** | HashiCorp Terraform | 1.8+ |
| **CI/CD Automation** | GitHub Actions | Standard Runner |
| **Testing** | JUnit 5, MockMvc, ArchUnit | 1.4.1 (ArchUnit) |

---

## Test Suite (12 Tests)

The workshop project includes a 12-test suite verifying health monitoring, domain records, and architectural boundaries:

1. **Model Invariant Tests (`DeploymentStatusTest.java` - 2 tests):**
   - Record construction, timestamp initialization, and null rejection.
2. **Service Unit Tests (`DeploymentHealthServiceTest.java` - 2 tests):**
   - Hostname resolution, version retrieval, and status generation.
3. **WebMvc Slice Tests (`AppControllerTest.java` - 2 tests):**
   - HTTP status code mapping (`200 OK`) and JSON payload validation.
4. **Architecture Governance (`CicdArchitectureTest.java` - 4 tests):**
   - ArchUnit rules asserting controller annotations, service annotations, and layer isolation.
5. **Context Integration Tests (`AppControllerIntegrationTest.java` - 2 tests):**
   - Spring Boot context boot and endpoint verification (`/api/status`, `/api/status/details`).

---

## Quick Start

### Prerequisites
- JDK 25 installed and configured (`JAVA_HOME`).
- Maven 3.9+ or Maven Wrapper (`./mvnw`).
- Docker installed (optional for local container testing).

### Build and Run Tests
```bash
mvn clean verify
```

### Run Locally
```bash
# 1. Run the Spring Boot application
mvn spring-boot:run

# 2. Check deployment status
curl -s http://localhost:8080/api/status

# 3. Check detailed deployment telemetry
curl -s http://localhost:8080/api/status/details
```

### Local Docker Swarm Simulation
```bash
# Initialize Swarm locally if not already active
docker swarm init

# Deploy the stack with 3 replicas and rolling update configuration
docker stack deploy -c docker-compose.yml cicd_demo
```
