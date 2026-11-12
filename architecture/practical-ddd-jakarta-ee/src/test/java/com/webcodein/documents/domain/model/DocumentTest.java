package com.webcodein.documents.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    @DisplayName("Should create draft document with valid UUID and title")
    void shouldCreateDraftDocument() {
        UUID id = UUID.randomUUID();
        DocumentTitle title = new DocumentTitle("Domain Driven Design Guide");
        Document document = new Document(id, title);

        assertEquals(id, document.getId());
        assertEquals(DocumentStatus.DRAFT, document.getStatus());
        assertEquals("Domain Driven Design Guide", document.getTitle().getValue());
    }

    @Test
    @DisplayName("Should reject document creation with null ID or null title")
    void shouldRejectNullIdOrTitle() {
        assertThrows(IllegalArgumentException.class, () -> new Document(null, new DocumentTitle("Valid")));
        assertThrows(IllegalArgumentException.class, () -> new Document(UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("Should publish draft document successfully")
    void shouldPublishDocument() {
        UUID id = UUID.randomUUID();
        Document document = new Document(id, new DocumentTitle("Valid Title"));

        document.publish();

        assertEquals(DocumentStatus.PUBLISHED, document.getStatus());
    }

    @Test
    @DisplayName("Should archive document successfully")
    void shouldArchiveDocument() {
        UUID id = UUID.randomUUID();
        Document document = new Document(id, new DocumentTitle("Valid Title"));

        document.archive();

        assertEquals(DocumentStatus.ARCHIVED, document.getStatus());
    }

    @Test
    @DisplayName("Should not allow publishing an archived document")
    void shouldNotPublishArchivedDocument() {
        UUID id = UUID.randomUUID();
        Document document = new Document(id, new DocumentTitle("Valid Title"));
        document.archive();

        IllegalStateException ex = assertThrows(IllegalStateException.class, document::publish);
        assertTrue(ex.getMessage().contains("Cannot publish an archived document"));
    }

    @Test
    @DisplayName("Should not allow re-archiving an archived document")
    void shouldNotReArchiveArchivedDocument() {
        UUID id = UUID.randomUUID();
        Document document = new Document(id, new DocumentTitle("Valid Title"));
        document.archive();

        IllegalStateException ex = assertThrows(IllegalStateException.class, document::archive);
        assertTrue(ex.getMessage().contains("Document is already archived"));
    }

    @Test
    @DisplayName("DocumentTitle value object must have at least 3 characters and trim whitespace")
    void documentTitleValidation() {
        assertThrows(IllegalArgumentException.class, () -> new DocumentTitle("ab"));
        assertThrows(IllegalArgumentException.class, () -> new DocumentTitle("   "));
        assertThrows(IllegalArgumentException.class, () -> new DocumentTitle(null));

        DocumentTitle valid = new DocumentTitle("  Clean Code  ");
        assertEquals("Clean Code", valid.getValue());
    }

    @Test
    @DisplayName("DocumentTitle value object equality based on value")
    void documentTitleEquality() {
        DocumentTitle title1 = new DocumentTitle("Architecture");
        DocumentTitle title2 = new DocumentTitle("Architecture");

        assertEquals(title1, title2);
        assertEquals(title1.hashCode(), title2.hashCode());
    }
}
