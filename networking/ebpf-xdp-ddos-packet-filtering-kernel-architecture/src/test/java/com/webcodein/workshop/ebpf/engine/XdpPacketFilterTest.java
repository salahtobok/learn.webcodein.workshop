package com.webcodein.workshop.ebpf.engine;

import com.webcodein.workshop.ebpf.model.FilterVerdict;
import com.webcodein.workshop.ebpf.model.IpPacket;
import com.webcodein.workshop.ebpf.model.XdpAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XdpPacketFilterTest {

    private final BpfMapRegistry registry = new BpfMapRegistry();
    private final XdpPacketFilter filter = new XdpPacketFilter(registry, 5); // 5 SYNs limit for testing

    @Test
    @DisplayName("Should pass legitimate HTTP TCP data packet")
    void shouldPassLegitimatePacket() {
        IpPacket packet = IpPacket.tcpData("192.0.2.1", "10.0.0.1", 45000, 443, 512);
        FilterVerdict verdict = filter.processPacket(packet);

        assertThat(verdict.action()).isEqualTo(XdpAction.XDP_PASS);
        assertThat(verdict.ruleMatched()).isEqualTo("TRAFFIC_CLEARED_TO_SKBUFF");
    }

    @Test
    @DisplayName("Should drop packet from blacklisted IP in O(1) time")
    void shouldDropBlacklistedIp() {
        registry.blacklistIp("198.51.100.99");
        IpPacket packet = IpPacket.tcpData("198.51.100.99", "10.0.0.1", 45000, 443, 512);

        FilterVerdict verdict = filter.processPacket(packet);
        assertThat(verdict.action()).isEqualTo(XdpAction.XDP_DROP);
        assertThat(verdict.ruleMatched()).isEqualTo("BPF_MAP_BLACKLIST_MATCH");
    }

    @Test
    @DisplayName("Should detect and drop SYN flood when rate limit threshold exceeded")
    void shouldDetectSynFlood() {
        String attackerIp = "203.0.113.88";

        // First 5 SYNs should pass
        for (int i = 0; i < 5; i++) {
            IpPacket syn = IpPacket.tcpSyn(attackerIp, "10.0.0.1", 50000 + i, 443);
            FilterVerdict verdict = filter.processPacket(syn);
            assertThat(verdict.action()).isEqualTo(XdpAction.XDP_PASS);
        }

        // 6th SYN exceeds threshold -> XDP_DROP
        IpPacket floodSyn = IpPacket.tcpSyn(attackerIp, "10.0.0.1", 50006, 443);
        FilterVerdict verdict = filter.processPacket(floodSyn);
        assertThat(verdict.action()).isEqualTo(XdpAction.XDP_DROP);
        assertThat(verdict.ruleMatched()).isEqualTo("SYN_FLOOD_THRESHOLD_EXCEEDED");
    }

    @Test
    @DisplayName("Should drop large UDP amplification flood packets")
    void shouldDropUdpAmplificationFlood() {
        IpPacket dnsAmplification = IpPacket.udp("198.51.100.1", "10.0.0.1", 53, 443, 2048);
        FilterVerdict verdict = filter.processPacket(dnsAmplification);

        assertThat(verdict.action()).isEqualTo(XdpAction.XDP_DROP);
        assertThat(verdict.ruleMatched()).isEqualTo("UDP_AMPLIFICATION_FLOOD_DETECTED");
    }

    @Test
    @DisplayName("Should drop malformed packets with invalid port numbers")
    void shouldDropMalformedPackets() {
        IpPacket malformed = new IpPacket("10.0.0.2", "10.0.0.1", 6, -1, 443, 100, false, false, System.nanoTime());
        FilterVerdict verdict = filter.processPacket(malformed);

        assertThat(verdict.action()).isEqualTo(XdpAction.XDP_DROP);
        assertThat(verdict.ruleMatched()).isEqualTo("MALFORMED_PACKET_HEADER");
    }
}
