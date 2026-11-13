package com.webcodein.workshop.ebpf.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BpfMapRegistryTest {

    private final BpfMapRegistry registry = new BpfMapRegistry();

    @Test
    @DisplayName("Should blacklist and remove IP from BPF map")
    void shouldBlacklistAndRemoveIp() {
        registry.blacklistIp("198.51.100.42");
        assertThat(registry.isBlacklisted("198.51.100.42")).isTrue();
        assertThat(registry.isBlacklisted("198.51.100.43")).isFalse();

        registry.removeBlacklistIp("198.51.100.42");
        assertThat(registry.isBlacklisted("198.51.100.42")).isFalse();
    }

    @Test
    @DisplayName("Should increment and track SYN rate counter")
    void shouldIncrementSynCounter() {
        assertThat(registry.incrementSynCounter("203.0.113.1")).isEqualTo(1);
        assertThat(registry.incrementSynCounter("203.0.113.1")).isEqualTo(2);

        registry.resetSynCounter("203.0.113.1");
        assertThat(registry.incrementSynCounter("203.0.113.1")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should track total dropped and passed packet metrics")
    void shouldTrackPacketMetrics() {
        registry.recordPacket(true);
        registry.recordPacket(true);
        registry.recordPacket(false);

        assertThat(registry.getTotalPacketsReceived()).isEqualTo(3);
        assertThat(registry.getTotalPacketsDropped()).isEqualTo(2);
        assertThat(registry.getTotalPacketsPassed()).isEqualTo(1);
    }
}
