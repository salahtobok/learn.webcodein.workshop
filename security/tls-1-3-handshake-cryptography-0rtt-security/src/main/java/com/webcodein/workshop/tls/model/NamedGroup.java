package com.webcodein.workshop.tls.model;

public enum NamedGroup {
    X25519("X25519", "XDH", 32),
    SECP256R1("secp256r1", "EC", 32),
    SECP384R1("secp384r1", "EC", 48);

    private final String standardName;
    private final String algorithmFamily;
    private final int keySizeBytes;

    NamedGroup(String standardName, String algorithmFamily, int keySizeBytes) {
        this.standardName = standardName;
        this.algorithmFamily = algorithmFamily;
        this.keySizeBytes = keySizeBytes;
    }

    public String getStandardName() {
        return standardName;
    }

    public String getAlgorithmFamily() {
        return algorithmFamily;
    }

    public int getKeySizeBytes() {
        return keySizeBytes;
    }
}
