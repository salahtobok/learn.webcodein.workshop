package com.webcodein.security.acl.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Document Entity Unit Tests")
class DocumentTest {

    @Test
    @DisplayName("Should create document with content constructor")
    void shouldCreateDocumentWithContent() {
        Document document = new Document("Confidential Patent Application");

        assertThat(document.getContent()).isEqualTo("Confidential Patent Application");
        assertThat(document.getId()).isNull();
    }

    @Test
    @DisplayName("Should mutate fields via setters")
    void shouldMutateFieldsViaSetters() {
        Document document = new Document();
        document.setId(55L);
        document.setContent("Revised Specifications");

        assertThat(document.getId()).isEqualTo(55L);
        assertThat(document.getContent()).isEqualTo("Revised Specifications");
    }
}
