package com.webcodein.workshop.quic.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PacketLossSimulatorTest {

    @Test
    @DisplayName("Should never drop packets when loss probability is 0.0")
    void shouldNeverDropAtZero() {
        PacketLossSimulator sim = new PacketLossSimulator(0.0);
        for (int i = 0; i < 100; i++) {
            assertThat(sim.shouldDrop()).isFalse();
        }
    }

    @Test
    @DisplayName("Should always drop packets when loss probability is 1.0")
    void shouldAlwaysDropAtOne() {
        PacketLossSimulator sim = new PacketLossSimulator(1.0);
        for (int i = 0; i < 100; i++) {
            assertThat(sim.shouldDrop()).isTrue();
        }
    }

    @Test
    @DisplayName("Should validate loss probability range [0.0, 1.0]")
    void shouldValidateRange() {
        assertThatThrownBy(() -> new PacketLossSimulator(-0.1))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new PacketLossSimulator(1.1))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
