package com.webcodein.jenkins;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JenkinsApplicationTests {

    public static GenericContainer<?> alpineContainer = new GenericContainer<>(DockerImageName.parse("alpine:3.18"))
            .withCommand("sleep", "30");

    @BeforeAll
    static void checkDockerAvailability() {
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), 
            "Skipping tests because Docker is not available locally.");
        
        alpineContainer.start();
    }

    @Test
    void testTestcontainersIsRunning() {
        assertTrue(alpineContainer.isRunning(), "The Alpine Testcontainer should be running!");
    }

    @Test
    void contextLoads() {
        // Just verify that the Spring Boot context successfully loads
    }
}
