package com.bookstore.infrastructure;

import com.bookstore.domain.ShippingDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LegacyShippingAclTest {

    @Test
    @DisplayName("Anti-Corruption Layer should translate legacy SOAP response into domain ShippingDetails")
    void shouldTranslateLegacyResponseIntoDomainModel() {
        LegacySoapShippingClient stubClient = isbn -> new LegacyShippingResponse("3.45", "Y");
        LegacyShippingAcl acl = new LegacyShippingAcl(stubClient);

        ShippingDetails details = acl.getShippingInfo("978-0134494166");

        assertThat(details.weightKg()).isEqualTo(3.45);
        assertThat(details.requiresSignature()).isTrue();
    }

    @Test
    @DisplayName("ACL correctly parses signature flag when legacy system returns N")
    void shouldHandleNonSignatureRequirement() {
        LegacySoapShippingClient stubClient = isbn -> new LegacyShippingResponse("1.20", "N");
        LegacyShippingAcl acl = new LegacyShippingAcl(stubClient);

        ShippingDetails details = acl.getShippingInfo("978-0321125217");

        assertThat(details.weightKg()).isEqualTo(1.20);
        assertThat(details.requiresSignature()).isFalse();
    }
}
