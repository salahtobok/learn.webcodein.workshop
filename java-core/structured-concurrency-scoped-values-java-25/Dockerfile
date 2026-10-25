FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /build
COPY . /build
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/structured-concurrency-scoped-values-java-25-1.0.0-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "--enable-preview", "-jar", "/app/app.jar"]
