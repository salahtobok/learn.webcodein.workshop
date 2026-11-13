package com.webcodein.workshop.ebpf.engine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class BpfMapRegistry {

    // BPF_MAP_TYPE_HASH: IP Blacklist Map
    private final ConcurrentMap<String, Boolean> blacklistMap = new ConcurrentHashMap<>();

    // BPF_MAP_TYPE_LRU_HASH: Per-IP SYN rate tracker (timestamp of last SYN + count in current window)
    private final ConcurrentMap<String, AtomicLong> synCounters = new ConcurrentHashMap<>();

    // BPF_MAP_TYPE_PERCPU_ARRAY: Metrics
    private final AtomicLong totalPacketsReceived = new AtomicLong();
    private final AtomicLong totalPacketsDropped = new AtomicLong();
    private final AtomicLong totalPacketsPassed = new AtomicLong();

    public void blacklistIp(String ip) {
        blacklistMap.put(ip, Boolean.TRUE);
    }

    public void removeBlacklistIp(String ip) {
        blacklistMap.remove(ip);
    }

    public boolean isBlacklisted(String ip) {
        return blacklistMap.containsKey(ip);
    }

    public long incrementSynCounter(String srcIp) {
        return synCounters.computeIfAbsent(srcIp, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void resetSynCounter(String srcIp) {
        synCounters.remove(srcIp);
    }

    public void recordPacket(boolean dropped) {
        totalPacketsReceived.incrementAndGet();
        if (dropped) {
            totalPacketsDropped.incrementAndGet();
        } else {
            totalPacketsPassed.incrementAndGet();
        }
    }

    public long getTotalPacketsReceived() {
        return totalPacketsReceived.get();
    }

    public long getTotalPacketsDropped() {
        return totalPacketsDropped.get();
    }

    public long getTotalPacketsPassed() {
        return totalPacketsPassed.get();
    }
}
