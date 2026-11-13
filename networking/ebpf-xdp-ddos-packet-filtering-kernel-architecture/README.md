# eBPF and XDP for DDoS Defense: High-Throughput Kernel Packet Filtering

Companion workshop project for the article: **eBPF and XDP for DDoS Defense: High-Throughput Kernel Packet Filtering for Web Infrastructure**.

## Tech Stack
- **Language**: Java 25
- **Architecture**: Domain Model, BPF Map Simulation, XDP Packet Filter Engine, ArchUnit
- **Testing**: JUnit 5, AssertJ, ArchUnit 1.4.1
- **Spring Boot**: None (pure Java 25 runtime)

## Architecture Overview
The project models the core architectural packet processing pipeline of eBPF (extended Berkeley Packet Filter) and XDP (eXpress Data Path):
1. **NIC/Driver-Level Hook**: Filtering packets at line rate before `sk_buff` memory allocations.
2. **BPF Map Lookups**: High-performance O(1) hash map lookups for real-time IP blacklisting.
3. **SYN Flood Mitigation**: In-kernel sliding window rate tracking and threshold enforcement.
4. **UDP Amplification Protection**: Immediate mitigation of volumetric DNS/NTP amplification bursts.
5. **Architectural Governance**: ArchUnit tests ensuring zero Spring Boot dependencies and clean domain isolation.

## Running Tests
```bash
mvn clean test
```
