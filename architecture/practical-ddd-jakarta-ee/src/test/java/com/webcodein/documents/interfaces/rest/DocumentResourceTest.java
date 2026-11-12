package com.webcodein.documents.interfaces.rest;

import com.webcodein.documents.application.service.DocumentService;
import com.webcodein.documents.domain.model.Document;
import com.webcodein.documents.domain.model.DocumentTitle;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentResourceTest {

    @Mock
    private DocumentService documentService;

    private DocumentResource documentResource;

    @BeforeEach
    void setUp() {
        documentResource = new DocumentResource(documentService);
    }

    @Test
    @DisplayName("POST /documents creates document and returns HTTP 201 Created")
    void shouldCreateDocument() {
        UUID id = UUID.randomUUID();
        Document doc = new Document(id, new DocumentTitle("API Specification"));
        when(documentService.createDocument("API Specification")).thenReturn(doc);

        Response response = documentResource.createDocument(new DocumentResource.DocumentRequest("API Specification"));

        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(DocumentResource.DocumentResponse.class);
        DocumentResource.DocumentResponse entity = (DocumentResource.DocumentResponse) response.getEntity();
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.title()).isEqualTo("API Specification");
        assertThat(entity.status()).isEqualTo("DRAFT");
    }

    @Test
    @DisplayName("GET /documents/{id} returns HTTP 200 with document when found")
    void shouldGetDocumentWhenFound() {
        UUID id = UUID.randomUUID();
        Document doc = new Document(id, new DocumentTitle("Existing Document"));
        when(documentService.getDocument(id)).thenReturn(doc);

        Response response = documentResource.getDocument(id);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        DocumentResource.DocumentResponse entity = (DocumentResource.DocumentResponse) response.getEntity();
        assertThat(entity.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("GET /documents/{id} returns HTTP 404 when document not found")
    void shouldReturn404WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(documentService.getDocument(id)).thenThrow(new IllegalArgumentException("Not found"));

        Response response = documentResource.getDocument(id);

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("POST /documents/{id}/publish returns HTTP 200 on success")
    void shouldPublishDocument() {
        UUID id = UUID.randomUUID();
        doNothing().when(documentService).publishDocument(id);

        Response response = documentResource.publishDocument(id);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(documentService).publishDocument(id);
    }

    @Test
    @DisplayName("POST /documents/{id}/archive returns HTTP 200 on success")
    void shouldArchiveDocument() {
        UUID id = UUID.randomUUID();
        doNothing().when(documentService).archiveDocument(id);

        Response response = documentResource.archiveDocument(id);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(documentService).archiveDocument(id);
    }
}
