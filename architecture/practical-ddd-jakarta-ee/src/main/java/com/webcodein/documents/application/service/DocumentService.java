package com.webcodein.documents.application.service;

import com.webcodein.documents.domain.model.Document;
import com.webcodein.documents.domain.model.DocumentTitle;
import com.webcodein.documents.domain.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Transactional
    public Document createDocument(String titleStr) {
        UUID id = UUID.randomUUID();
        DocumentTitle title = new DocumentTitle(titleStr);
        Document document = new Document(id, title);
        documentRepository.insert(document);
        return document;
    }

    @Transactional
    public void publishDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        document.publish();
        documentRepository.update(document);
    }

    @Transactional
    public void archiveDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        document.archive();
        documentRepository.update(document);
    }

    @Transactional(readOnly = true)
    public Document getDocument(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));
    }
}
