FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY target/spring-boot-4-kubernetes-graceful-shutdown-cgroups-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
