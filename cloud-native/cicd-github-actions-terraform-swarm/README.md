# CI/CD DevOps: Zero-Downtime Deployments with GitHub Actions, Terraform, and Docker Swarm

This project is the companion code for the article **"CI/CD DevOps: Zero-Downtime Deployments with GitHub Actions, Terraform, and Docker Swarm"** on [Webcodein](https://learn.webcodein.com/post/cicd-github-actions-terraform-swarm).

## Prerequisites
- Java 25 (e.g., Temurin or Corretto)
- Maven 3.9+
- Docker & Docker Compose
- Terraform (Optional, for AWS provisioning)

## Running Locally via Maven

To build and run the application using Maven:

1. **Build the project:**
   ```bash
   ./mvnw clean verify
   ```
   This will compile the application and execute the unit and integration tests.

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Verify:**
   Open a browser or use cURL:
   ```bash
   curl http://localhost:8080/api/status
   curl http://localhost:8080/actuator/health
   ```

## Running via Docker Compose (Simulating Swarm Deploy)

1. **Build the image and start the cluster:**
   ```bash
   docker-compose up --build -d
   ```
   *Note: In standard Docker Compose, the `deploy` key is partially ignored (like `replicas`), but it will still build and start the application container.*

2. **Testing Swarm Locally:**
   If you have Docker Swarm initialized locally (`docker swarm init`), you can deploy this exact compose file as a stack:
   ```bash
   docker stack deploy -c docker-compose.yml my_stack
   ```
   This will spin up 3 replicas with rolling update policies in place.

## CI/CD Pipeline
An example GitHub Actions workflow is provided in the article. It leverages Terraform for infrastructure provisioning and GitHub Actions for continuous integration and deployment.
