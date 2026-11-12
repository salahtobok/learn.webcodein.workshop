package com.webcodein.jenkins.service;

import com.webcodein.jenkins.model.BuildPipelineStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PipelineStatusServiceTest {

    private final PipelineStatusService service = new PipelineStatusService();

    @Test
    @DisplayName("Should return valid build pipeline status report")
    void shouldReturnValidBuildPipelineStatus() {
        BuildPipelineStatus status = service.getCurrentStatus();

        assertThat(status).isNotNull();
        assertThat(status.application()).isEqualTo("jenkins-pipeline-spring-boot-4-graalvm");
        assertThat(status.status()).isEqualTo("UP");
        assertThat(status.pipelineEngine()).isEqualTo("Jenkins Declarative Pipeline");
        assertThat(status.jvmRuntime()).isNotEmpty();
        assertThat(status.graalvmNativeTarget()).contains("Native Executable");
        assertThat(status.checkedAt()).isNotNull();
    }
}
