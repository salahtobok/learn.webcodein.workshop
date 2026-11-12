package com.webcodein.flyway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerRequestTest {

    @Test
    @DisplayName("Should accept valid email and full name")
    void shouldAcceptValidRequest() {
        CustomerRequest req = new CustomerRequest("user@webcodein.com", "Sarah Connor");
        assertThat(req.email()).isEqualTo("user@webcodein.com");
        assertThat(req.fullName()).isEqualTo("Sarah Connor");
    }

    @Test
    @DisplayName("Should reject null or blank email")
    void shouldRejectInvalidEmail() {
        assertThatThrownBy(() -> new CustomerRequest(null, "Sarah Connor"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CustomerRequest("", "Sarah Connor"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should reject null or blank full name")
    void shouldRejectInvalidFullName() {
        assertThatThrownBy(() -> new CustomerRequest("user@webcodein.com", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CustomerRequest("user@webcodein.com", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
