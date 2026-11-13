package com.webcodein.workshop.tls.model;

public enum CipherSuite {
    TLS_AES_128_GCM_SHA256("TLS_AES_128_GCM_SHA256", TlsVersion.TLS_1_3, "AES", 16, "HmacSHA256", true),
    TLS_AES_256_GCM_SHA384("TLS_AES_256_GCM_SHA384", TlsVersion.TLS_1_3, "AES", 32, "HmacSHA384", true),
    TLS_CHACHA20_POLY1305_SHA256("TLS_CHACHA20_POLY1305_SHA256", TlsVersion.TLS_1_3, "ChaCha20", 32, "HmacSHA256", true),
    TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", TlsVersion.TLS_1_2, "AES", 16, "HmacSHA256", false),
    TLS_RSA_WITH_AES_128_CBC_SHA("TLS_RSA_WITH_AES_128_CBC_SHA", TlsVersion.TLS_1_2, "AES", 16, "HmacSHA1", false);

    private final String standardName;
    private final TlsVersion version;
    private final String symmetricAlgorithm;
    private final int keyLengthBytes;
    private final String hmacAlgorithm;
    private final boolean forwardSecrecy;

    CipherSuite(String standardName, TlsVersion version, String symmetricAlgorithm,
                int keyLengthBytes, String hmacAlgorithm, boolean forwardSecrecy) {
        this.standardName = standardName;
        this.version = version;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.keyLengthBytes = keyLengthBytes;
        this.hmacAlgorithm = hmacAlgorithm;
        this.forwardSecrecy = forwardSecrecy;
    }

    public String getStandardName() {
        return standardName;
    }

    public TlsVersion getVersion() {
        return version;
    }

    public String getSymmetricAlgorithm() {
        return symmetricAlgorithm;
    }

    public int getKeyLengthBytes() {
        return keyLengthBytes;
    }

    public String getHmacAlgorithm() {
        return hmacAlgorithm;
    }

    public boolean hasForwardSecrecy() {
        return forwardSecrecy;
    }

    public boolean isTls13Allowed() {
        return this.version == TlsVersion.TLS_1_3 && this.forwardSecrecy;
    }
}
