# Workshop: GraalVM Native Image with Spring Boot 4: Cloud-Native Java

Companion code for the in-depth technical guide: [GraalVM Native Image with Spring Boot 4: A Practical Guide to Cloud-Native Java](https://learn.webcodein.com/post/graalvm-native-image-spring-boot-4-cloud-native-java-guide).

## Overview

This companion project demonstrates how to build, test, and containerize a production-ready cloud-native microservice using **Spring Boot 4.0.0**, **Java 25**, and **GraalVM Native Image (SubstrateVM)**. It illustrates Ahead-of-Time (AOT) compilation, custom runtime hint registration, and cloud-native packaging via Cloud Native Buildpacks and multi-stage Docker builds.

## Project Structure

```text
workshop/cloud-native/graalvm-native-image-spring-boot/
├── pom.xml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/webcodein/nativedemo/
│   │   │   ├── NativeDemoApplication.java   # Spring Boot entry point & @ImportRuntimeHints
│   │   │   ├── GreetingController.java       # REST endpoint (GET /greet/{name})
│   │   │   ├── MyDynamicClass.java           # Reflectively accessed domain class
│   │   │   └── MyHints.java                  # RuntimeHintsRegistrar implementation
│   │   └── resources/
│   │       ├── application.properties        # Application configuration & virtual threads
│   │       └── config/
│   │           └── app.json                  # Dynamic resource registered via hints
│   └── test/
│       └── java/com/webcodein/nativedemo/
│           ├── GreetingControllerTest.java    # Web-layer MockMvc test
│           ├── NativeDemoApplicationTests.java# Spring context initialization test
│           └── MyHintsTest.java               # RuntimeHintsPredicates validation
└── README.md
```

## Key Architectural Concepts

1. **Ahead-of-Time (AOT) Processing**: Spring Boot 4 evaluates `@Configuration` classes, computes bean definitions, and pre-generates reflection/proxy metadata during build time.
2. **Closed-World Assumption**: GraalVM static analysis requires all reachable classes, reflective invocations, and classpath resources to be declared ahead of time.
3. **RuntimeHints API**: Custom `RuntimeHintsRegistrar` implementation registering reflective constructor/method invocations for `MyDynamicClass` and classpath resource pattern `config/*.json`.
4. **Zero-Downtime Containerization**: Multi-stage Docker builds linking required system libraries (`libz.so.1`) into ultra-minimal Google Distroless runtime images (~50-80 MB).

## Prerequisites

- **Java Development Kit (JDK)**: JDK 25 (GraalVM for JDK 25 recommended for local native compilation)
- **Apache Maven**: 3.9+ (or use the included `./mvnw` wrapper)
- **Docker**: Required for Cloud Native Buildpacks (`spring-boot:build-image`) and multi-stage container builds

## Test Suite Verification

The project includes unit, mock MVC, and AOT hint predicate tests that run under standard JVM execution without needing a full 10-minute native compilation:

```bash
mvn clean test
# or
mvn -B -ntp verify
```

### Test Breakdown

- `GreetingControllerTest`: Validates the REST endpoint `GET /greet/{name}` using `MockMvc`, ensuring correct HTTP status (200 OK) and response payload structure.
- `NativeDemoApplicationTests`: Verifies that the Spring Boot application context loads cleanly under Spring Boot 4.0.0.
- `MyHintsTest`: Uses `RuntimeHintsPredicates` to assert that:
  - Reflection hints are properly registered for `MyDynamicClass` constructors and public methods.
  - Resource pattern hints are properly registered for `config/app.json`.

## Building the Native Binary

### Option A: Local Compilation (Requires GraalVM JDK 25)

```bash
./mvnw -Pnative native:compile
```

The compiled standalone executable will be located at:
```bash
./target/native-demo
```

Execute the binary:
```bash
./target/native-demo
# Expected output:
# Started NativeDemoApplication in 0.169 seconds (process running for 0.174)
```

### Option B: Cloud Native Buildpacks (Docker Only)

No local GraalVM installation is required. Docker builds the native image inside a standardized Paketo/Liberica NIK container:

```bash
./mvnw -Pnative spring-boot:build-image
```

Run the containerized image:
```bash
docker run --rm -p 8080:8080 native-demo:0.0.1-SNAPSHOT
```

### Option C: Multi-Stage Docker Build

```bash
docker build -t native-demo:distroless .
docker run --rm -p 8080:8080 native-demo:distroless
```

> **Note on `libz.so.1`**: The native binary links against `zlib`. Because `gcr.io/distroless/base-debian12` does not include `libz.so.1`, the Dockerfile copies `/usr/lib64/libz.so.1` from the builder stage to `/lib/x86_64-linux-gnu/libz.so.1` to prevent startup linking errors.

## Performance Benchmark Comparison

| Metric | Traditional JVM (HotSpot) | GraalVM Native Image | Cloud-Native Impact |
| :--- | :--- | :--- | :--- |
| **Startup Time** | 2.5 – 3.7 seconds | 0.14 – 0.17 seconds | ~18x faster cold start; instant K8s readiness |
| **Memory (RSS)** | 280 – 350 MB | 45 – 65 MB | ~75% RAM reduction; higher pod density |
| **Artifact Size** | ~20 MB (JAR) + 300 MB JRE | ~70 MB standalone binary | Minimal attack surface; fast CI/CD pushes |
| **Packaging** | JRE Base Container (~300MB) | Distroless Container (~75MB) | Zero shell/package manager vulnerability |

## Related Resources

- Live Article: [GraalVM Native Image with Spring Boot 4: A Practical Guide to Cloud-Native Java](https://learn.webcodein.com/post/graalvm-native-image-spring-boot-4-cloud-native-java-guide)
- Parent Repository: [learn.webcodein.com](https://github.com/salahtobok/learn.webcodein.com)
