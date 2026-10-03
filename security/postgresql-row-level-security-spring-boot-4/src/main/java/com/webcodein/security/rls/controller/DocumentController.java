package com.webcodein.security.rls.controller;

import com.webcodein.security.rls.entity.Document;
import com.webcodein.security.rls.repository.DocumentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostMapping
    public Document createDocument(@RequestBody Document document, Authentication authentication) {
        // We set the tenant ID on creation. RLS will verify it automatically.
        document.setTenantId(authentication.getName());
        return documentRepository.save(document);
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        // Notice we do NOT filter by tenant ID in Java. 
        // PostgreSQL RLS handles the filtering automatically at the database level!
        return documentRepository.findAll();
    }
}
