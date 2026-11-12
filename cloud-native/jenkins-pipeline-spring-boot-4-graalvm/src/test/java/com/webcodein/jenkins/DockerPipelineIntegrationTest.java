package com.webcodein.jenkins;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

class DockerPipelineIntegrationTest {

    @Test
    @DisplayName("Verify sibling Docker container execution in pipeline environment")
    void shouldExecuteDockerContainerWhenAvailable() {
        boolean dockerAvailable = false;
        try {
            dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable t) {
            dockerAvailable = false;
        }

        Assumptions.assumeTrue(dockerAvailable, "Docker daemon is not accessible in this environment; skipping sibling container test");

        try (GenericContainer<?> alpine = new GenericContainer<>(DockerImageName.parse("alpine:3.20"))
                .withCommand("sleep", "30")) {
            alpine.start();
            assertThat(alpine.isRunning()).isTrue();
        }
    }
}
