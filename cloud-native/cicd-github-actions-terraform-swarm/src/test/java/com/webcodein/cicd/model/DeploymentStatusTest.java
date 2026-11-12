package com.webcodein.cicd.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeploymentStatusTest {

    @Test
    @DisplayName("Should create valid deployment status")
    void shouldCreateValidStatus() {
        DeploymentStatus status = DeploymentStatus.up("1.0.0", "swarm-worker-01");
        assertThat(status.status()).isEqualTo("UP");
        assertThat(status.version()).isEqualTo("1.0.0");
        assertThat(status.hostname()).isEqualTo("swarm-worker-01");
        assertThat(status.message()).contains("Zero-downtime deployment");
        assertThat(status.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should reject null fields")
    void shouldRejectNullFields() {
        assertThatThrownBy(() -> new DeploymentStatus(null, "1.0.0", "host", "msg", Instant.now()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeploymentStatus("UP", null, "host", "msg", Instant.now()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeploymentStatus("UP", "1.0.0", null, "msg", Instant.now()))
                .isInstanceOf(NullPointerException.class);
    }
}
