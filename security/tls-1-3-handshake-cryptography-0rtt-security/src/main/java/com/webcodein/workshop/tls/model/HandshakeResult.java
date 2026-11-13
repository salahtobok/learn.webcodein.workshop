package com.webcodein.workshop.tls.model;

import java.util.Arrays;
import java.util.Objects;

public record HandshakeResult(
        TlsVersion version,
        CipherSuite cipherSuite,
        int roundTrips,
        byte[] clientTrafficSecret,
        byte[] serverTrafficSecret,
        boolean earlyDataAccepted,
        String sessionTicketId
) {
    public HandshakeResult {
        Objects.requireNonNull(version, "version must not be null");
        Objects.requireNonNull(cipherSuite, "cipherSuite must not be null");
        Objects.requireNonNull(clientTrafficSecret, "clientTrafficSecret must not be null");
        Objects.requireNonNull(serverTrafficSecret, "serverTrafficSecret must not be null");
        clientTrafficSecret = clientTrafficSecret.clone();
        serverTrafficSecret = serverTrafficSecret.clone();
    }

    @Override
    public byte[] clientTrafficSecret() {
        return clientTrafficSecret.clone();
    }

    @Override
    public byte[] serverTrafficSecret() {
        return serverTrafficSecret.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandshakeResult that)) return false;
        return roundTrips == that.roundTrips &&
                earlyDataAccepted == that.earlyDataAccepted &&
                version == that.version &&
                cipherSuite == that.cipherSuite &&
                Arrays.equals(clientTrafficSecret, that.clientTrafficSecret) &&
                Arrays.equals(serverTrafficSecret, that.serverTrafficSecret) &&
                Objects.equals(sessionTicketId, that.sessionTicketId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version, cipherSuite, roundTrips, earlyDataAccepted, sessionTicketId);
        result = 31 * result + Arrays.hashCode(clientTrafficSecret);
        result = 31 * result + Arrays.hashCode(serverTrafficSecret);
        return result;
    }
}
