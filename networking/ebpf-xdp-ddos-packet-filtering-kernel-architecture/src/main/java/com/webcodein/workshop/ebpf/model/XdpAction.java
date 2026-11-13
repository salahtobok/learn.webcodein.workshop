package com.webcodein.workshop.ebpf.model;

public enum XdpAction {
    XDP_DROP,      // Drop packet immediately at the NIC/driver layer
    XDP_TX,        // Bounce packet back out the same interface (fast reflection/forwarding)
    XDP_REDIRECT,  // Forward packet to another NIC, CPU core, or AF_XDP socket
    XDP_PASS,      // Allow packet to proceed up to the standard Linux TCP/IP stack (sk_buff)
    XDP_ABORTED    // Filter error / exception
}
