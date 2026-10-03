package com.webcodein.security.acl.controller;

import com.webcodein.security.acl.entity.Document;
import com.webcodein.security.acl.service.DocumentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Document createDocument(@RequestBody Document document, Authentication authentication) {
        return documentService.createDocument(document, authentication.getName());
    }

    @GetMapping("/{id}")
    public Document getDocument(@PathVariable Long id) {
        return documentService.getDocument(id);
    }

    @PutMapping("/{id}")
    public Document updateDocument(@PathVariable Long id, @RequestBody Document document) {
        return documentService.updateDocument(id, document.getContent());
    }
}
