package com.webcodein.documents.application.service;

import com.webcodein.documents.domain.model.Document;
import com.webcodein.documents.domain.model.DocumentStatus;
import com.webcodein.documents.domain.model.DocumentTitle;
import com.webcodein.documents.domain.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(documentRepository);
    }

    @Test
    @DisplayName("Should create and insert document in draft status")
    void shouldCreateDocument() {
        Document created = documentService.createDocument("Enterprise Architecture Guide");

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle().getValue()).isEqualTo("Enterprise Architecture Guide");
        assertThat(created.getStatus()).isEqualTo(DocumentStatus.DRAFT);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepository).insert(captor.capture());
        assertThat(captor.getValue().getTitle().getValue()).isEqualTo("Enterprise Architecture Guide");
    }

    @Test
    @DisplayName("Should publish document and persist updated status")
    void shouldPublishDocument() {
        UUID docId = UUID.randomUUID();
        Document doc = new Document(docId, new DocumentTitle("Draft Spec"));
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        documentService.publishDocument(docId);

        assertThat(doc.getStatus()).isEqualTo(DocumentStatus.PUBLISHED);
        verify(documentRepository).update(doc);
    }

    @Test
    @DisplayName("Should archive document and persist updated status")
    void shouldArchiveDocument() {
        UUID docId = UUID.randomUUID();
        Document doc = new Document(docId, new DocumentTitle("Obsolete Spec"));
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        documentService.archiveDocument(docId);

        assertThat(doc.getStatus()).isEqualTo(DocumentStatus.ARCHIVED);
        verify(documentRepository).update(doc);
    }

    @Test
    @DisplayName("Should retrieve document by ID when it exists")
    void shouldRetrieveDocument() {
        UUID docId = UUID.randomUUID();
        Document doc = new Document(docId, new DocumentTitle("Found Spec"));
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        Document found = documentService.getDocument(docId);

        assertThat(found).isEqualTo(doc);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when document not found")
    void shouldThrowWhenDocumentNotFound() {
        UUID missingId = UUID.randomUUID();
        when(documentRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocument(missingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Document not found with ID");
    }
}
