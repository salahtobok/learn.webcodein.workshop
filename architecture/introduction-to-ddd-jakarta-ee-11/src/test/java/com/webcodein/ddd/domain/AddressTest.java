package com.webcodein.ddd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    @Test
    @DisplayName("Two address value objects with identical attributes should be equal")
    void shouldBeEqualWhenAttributesMatch() {
        Address addr1 = new Address("123 Main St", "Tech City", "10001");
        Address addr2 = new Address("123 Main St", "Tech City", "10001");

        assertThat(addr1).isEqualTo(addr2);
        assertThat(addr1.hashCode()).isEqualTo(addr2.hashCode());
    }

    @Test
    @DisplayName("Addresses with different attributes should not be equal")
    void shouldNotBeEqualWhenAttributesDiffer() {
        Address addr1 = new Address("123 Main St", "Tech City", "10001");
        Address addr2 = new Address("456 Elm St", "Tech City", "10001");

        assertThat(addr1).isNotEqualTo(addr2);
    }

    @Test
    @DisplayName("Address creation should reject null attributes")
    void shouldRejectNullAttributes() {
        assertThatThrownBy(() -> new Address(null, "City", "12345"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Street must not be null");

        assertThatThrownBy(() -> new Address("Street", null, "12345"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("City must not be null");

        assertThatThrownBy(() -> new Address("Street", "City", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ZipCode must not be null");
    }
}
