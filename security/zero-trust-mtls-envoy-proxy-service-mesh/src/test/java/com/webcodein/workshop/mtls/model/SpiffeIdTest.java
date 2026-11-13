package com.webcodein.workshop.mtls.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpiffeIdTest {

    @Test
    @DisplayName("Should parse valid SPIFFE ID URI into record components")
    void shouldParseValidSpiffeId() {
        String uri = "spiffe://prod.us-east.mesh/ns/finance/sa/payment-engine";
        SpiffeId id = SpiffeId.parse(uri);

        assertThat(id.trustDomain()).isEqualTo("prod.us-east.mesh");
        assertThat(id.namespace()).isEqualTo("finance");
        assertThat(id.serviceAccount()).isEqualTo("payment-engine");
        assertThat(id.toUriString()).isEqualTo(uri);
    }

    @Test
    @DisplayName("Should reject malformed SPIFFE ID formats missing required segments")
    void shouldRejectMalformedSpiffeIds() {
        assertThatThrownBy(() -> SpiffeId.parse("http://cluster.local/ns/prod/sa/order"))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> SpiffeId.parse("spiffe://cluster.local/invalid/prod"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
