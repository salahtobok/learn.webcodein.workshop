package com.webcodein.workshop.tls.model;

public enum TlsVersion {
    TLS_1_2("TLSv1.2", 2, false),
    TLS_1_3("TLSv1.3", 1, true);

    private final String standardName;
    private final int defaultHandshakeRtt;
    private final boolean supportsZeroRtt;

    TlsVersion(String standardName, int defaultHandshakeRtt, boolean supportsZeroRtt) {
        this.standardName = standardName;
        this.defaultHandshakeRtt = defaultHandshakeRtt;
        this.supportsZeroRtt = supportsZeroRtt;
    }

    public String getStandardName() {
        return standardName;
    }

    public int getDefaultHandshakeRtt() {
        return defaultHandshakeRtt;
    }

    public boolean isSupportsZeroRtt() {
        return supportsZeroRtt;
    }
}
