package com.webcodein.workshop.quic.engine;

import java.util.Random;

/**
 * Deterministic or stochastic network loss generator for transport simulations.
 */
public class PacketLossSimulator {

    private final double lossProbability;
    private final Random random;

    public PacketLossSimulator(double lossProbability) {
        if (lossProbability < 0.0 || lossProbability > 1.0) {
            throw new IllegalArgumentException("Loss probability must be between 0.0 and 1.0");
        }
        this.lossProbability = lossProbability;
        this.random = new Random(42L); // Fixed seed for reproducible benchmarks
    }

    public PacketLossSimulator(double lossProbability, long seed) {
        this.lossProbability = lossProbability;
        this.random = new Random(seed);
    }

    public boolean shouldDrop() {
        if (lossProbability <= 0.0) return false;
        if (lossProbability >= 1.0) return true;
        return random.nextDouble() < lossProbability;
    }
}
