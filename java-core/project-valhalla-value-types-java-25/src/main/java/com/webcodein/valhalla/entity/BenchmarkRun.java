package com.webcodein.valhalla.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class BenchmarkRun {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private long throughput;

    public BenchmarkRun() {}
    public BenchmarkRun(String name, long throughput) {
        this.name = name;
        this.throughput = throughput;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getThroughput() { return throughput; }
    public void setThroughput(long throughput) { this.throughput = throughput; }
}
