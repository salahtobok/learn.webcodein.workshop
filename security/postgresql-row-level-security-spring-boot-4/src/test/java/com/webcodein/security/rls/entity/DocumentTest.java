package com.webcodein.security.rls.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Document Entity Unit Tests")
class DocumentTest {

    @Test
    @DisplayName("Should create document with parameterized constructor")
    void shouldCreateDocumentWithParameters() {
        Document document = new Document("Confidential Report", "tenant_alpha");

        assertThat(document.getContent()).isEqualTo("Confidential Report");
        assertThat(document.getTenantId()).isEqualTo("tenant_alpha");
        assertThat(document.getId()).isNull();
    }

    @Test
    @DisplayName("Should mutate fields via setters")
    void shouldMutateFieldsViaSetters() {
        Document document = new Document();
        document.setId(101L);
        document.setContent("Updated Body");
        document.setTenantId("tenant_beta");

        assertThat(document.getId()).isEqualTo(101L);
        assertThat(document.getContent()).isEqualTo("Updated Body");
        assertThat(document.getTenantId()).isEqualTo("tenant_beta");
    }
}
