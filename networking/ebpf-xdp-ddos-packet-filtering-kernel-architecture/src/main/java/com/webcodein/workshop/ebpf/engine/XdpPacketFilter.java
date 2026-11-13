package com.webcodein.workshop.ebpf.engine;

import com.webcodein.workshop.ebpf.model.FilterVerdict;
import com.webcodein.workshop.ebpf.model.IpPacket;
import com.webcodein.workshop.ebpf.model.XdpAction;

import java.util.Objects;

public class XdpPacketFilter {

    private final BpfMapRegistry bpfMapRegistry;
    private final int synRateLimitThreshold;

    public XdpPacketFilter(BpfMapRegistry bpfMapRegistry) {
        this(bpfMapRegistry, 100); // 100 SYNs max per window
    }

    public XdpPacketFilter(BpfMapRegistry bpfMapRegistry, int synRateLimitThreshold) {
        this.bpfMapRegistry = Objects.requireNonNull(bpfMapRegistry, "bpfMapRegistry must not be null");
        this.synRateLimitThreshold = synRateLimitThreshold;
    }

    public FilterVerdict processPacket(IpPacket packet) {
        long startNanos = System.nanoTime();

        // 1. Sanity check (Kernel boundary validation)
        if (packet.srcPort() <= 0 || packet.dstPort() <= 0 || packet.lengthBytes() <= 0) {
            bpfMapRegistry.recordPacket(true);
            return new FilterVerdict(XdpAction.XDP_DROP, "MALFORMED_PACKET_HEADER", System.nanoTime() - startNanos);
        }

        // 2. O(1) BPF Blacklist Map Lookup
        if (bpfMapRegistry.isBlacklisted(packet.srcIp())) {
            bpfMapRegistry.recordPacket(true);
            return new FilterVerdict(XdpAction.XDP_DROP, "BPF_MAP_BLACKLIST_MATCH", System.nanoTime() - startNanos);
        }

        // 3. SYN Flood Mitigation
        if (packet.protocol() == 6 && packet.isSynFlag() && !packet.isAckFlag()) {
            long synCount = bpfMapRegistry.incrementSynCounter(packet.srcIp());
            if (synCount > synRateLimitThreshold) {
                bpfMapRegistry.recordPacket(true);
                return new FilterVerdict(XdpAction.XDP_DROP, "SYN_FLOOD_THRESHOLD_EXCEEDED", System.nanoTime() - startNanos);
            }
        }

        // 4. UDP Amplification Flood Mitigation (Large packets targeting privileged ports)
        if (packet.protocol() == 17 && packet.lengthBytes() > 1400 && (packet.srcPort() == 53 || packet.srcPort() == 123 || packet.srcPort() == 1900)) {
            bpfMapRegistry.recordPacket(true);
            return new FilterVerdict(XdpAction.XDP_DROP, "UDP_AMPLIFICATION_FLOOD_DETECTED", System.nanoTime() - startNanos);
        }

        // 5. Normal traffic: PASS to Linux network stack
        bpfMapRegistry.recordPacket(false);
        return new FilterVerdict(XdpAction.XDP_PASS, "TRAFFIC_CLEARED_TO_SKBUFF", System.nanoTime() - startNanos);
    }
}
