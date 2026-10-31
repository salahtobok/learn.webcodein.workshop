package com.webcodein.shop.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductMetadataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should instantiate record with valid properties and uphold record contracts")
    void recordPropertiesAndContract() {
        ProductMetadata metadata = new ProductMetadata("Acme Corp", 3, List.of("electronics", "hardware"));

        assertThat(metadata.manufacturer()).isEqualTo("Acme Corp");
        assertThat(metadata.warrantyYears()).isEqualTo(3);
        assertThat(metadata.tags()).containsExactly("electronics", "hardware");

        ProductMetadata copy = new ProductMetadata("Acme Corp", 3, List.of("electronics", "hardware"));
        assertThat(metadata).isEqualTo(copy);
        assertThat(metadata.hashCode()).isEqualTo(copy.hashCode());
        assertThat(metadata.toString()).contains("Acme Corp").contains("3");
    }

    @Test
    @DisplayName("Should serialize to JSON and deserialize back from JSON using Jackson")
    void jsonSerializationRoundTrip() throws Exception {
        ProductMetadata original = new ProductMetadata("TechBrand", 2, List.of("laptop", "usb-c"));

        String json = objectMapper.writeValueAsString(original);
        assertThat(json).contains("\"manufacturer\":\"TechBrand\"")
                .contains("\"warrantyYears\":2")
                .contains("\"tags\":[\"laptop\",\"usb-c\"]");

        ProductMetadata deserialized = objectMapper.readValue(json, ProductMetadata.class);
        assertThat(deserialized).isEqualTo(original);
    }
}
