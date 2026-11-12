package com.webcodein.cicd.service;

import com.webcodein.cicd.model.DeploymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentHealthServiceTest {

    @Test
    @DisplayName("Should generate current deployment status with configured version")
    void shouldReturnCurrentStatus() {
        DeploymentHealthService service = new DeploymentHealthService("2.5.0");
        DeploymentStatus status = service.getCurrentStatus();

        assertThat(status.status()).isEqualTo("UP");
        assertThat(status.version()).isEqualTo("2.5.0");
        assertThat(status.hostname()).isNotBlank();
        assertThat(service.getVersion()).isEqualTo("2.5.0");
    }

    @Test
    @DisplayName("Should resolve hostname safely")
    void shouldResolveHostname() {
        DeploymentHealthService service = new DeploymentHealthService("1.0.0");
        String host = service.resolveHostname();
        assertThat(host).isNotBlank();
    }
}
