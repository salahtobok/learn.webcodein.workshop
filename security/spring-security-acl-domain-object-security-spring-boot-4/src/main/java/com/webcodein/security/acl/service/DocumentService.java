package com.webcodein.security.acl.service;

import com.webcodein.security.acl.entity.Document;
import com.webcodein.security.acl.repository.DocumentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MutableAclService aclService;

    public DocumentService(DocumentRepository documentRepository, MutableAclService aclService) {
        this.documentRepository = documentRepository;
        this.aclService = aclService;
    }

    @Transactional
    public Document createDocument(Document document, String username) {
        Document savedDoc = documentRepository.save(document);

        ObjectIdentity oi = new ObjectIdentityImpl(Document.class, savedDoc.getId());
        Sid sid = new PrincipalSid(username);

        MutableAcl acl = aclService.createAcl(oi);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sid, true);
        aclService.updateAcl(acl);

        return savedDoc;
    }

    @PreAuthorize("hasPermission(#id, 'com.webcodein.security.acl.entity.Document', 'READ')")
    public Document getDocument(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
    }

    @PreAuthorize("hasPermission(#id, 'com.webcodein.security.acl.entity.Document', 'WRITE')")
    public Document updateDocument(Long id, String content) {
        Document doc = getDocument(id);
        doc.setContent(content);
        return documentRepository.save(doc);
    }
}
