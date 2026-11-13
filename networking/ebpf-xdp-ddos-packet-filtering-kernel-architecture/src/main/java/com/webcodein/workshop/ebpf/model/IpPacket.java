package com.webcodein.workshop.ebpf.model;

import java.util.Objects;

public record IpPacket(
        String srcIp,
        String dstIp,
        int protocol, // 6 = TCP, 17 = UDP, 1 = ICMP
        int srcPort,
        int dstPort,
        int lengthBytes,
        boolean isSynFlag,
        boolean isAckFlag,
        long timestampNanos
) {
    public IpPacket {
        Objects.requireNonNull(srcIp, "srcIp must not be null");
        Objects.requireNonNull(dstIp, "dstIp must not be null");
        if (lengthBytes < 0) {
            throw new IllegalArgumentException("lengthBytes must not be negative");
        }
    }

    public static IpPacket tcpSyn(String srcIp, String dstIp, int srcPort, int dstPort) {
        return new IpPacket(srcIp, dstIp, 6, srcPort, dstPort, 60, true, false, System.nanoTime());
    }

    public static IpPacket tcpData(String srcIp, String dstIp, int srcPort, int dstPort, int length) {
        return new IpPacket(srcIp, dstIp, 6, srcPort, dstPort, length, false, true, System.nanoTime());
    }

    public static IpPacket udp(String srcIp, String dstIp, int srcPort, int dstPort, int length) {
        return new IpPacket(srcIp, dstIp, 17, srcPort, dstPort, length, false, false, System.nanoTime());
    }
}
