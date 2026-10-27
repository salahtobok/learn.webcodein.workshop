package com.webcodein.security.acl;

import com.webcodein.security.acl.controller.DocumentController;
import com.webcodein.security.acl.entity.Document;
import com.webcodein.security.acl.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class AclDemoApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentController documentController;

    @Test
    void testAclIsolationBetweenUsers() {
        // 1. Authenticate as user1 and create a document
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
        Document doc = documentService.createDocument(new Document("User 1 Confidential"), "user1");
        assertNotNull(doc.getId());

        // 2. user1 can read own document
        Document readByOwner = documentService.getDocument(doc.getId());
        assertEquals("User 1 Confidential", readByOwner.getContent());

        // 3. user1 can update own document
        Document updated = documentService.updateDocument(doc.getId(), "User 1 Updated Content");
        assertEquals("User 1 Updated Content", updated.getContent());

        // 4. Authenticate as user2
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user2", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );

        // 5. user2 must be denied READ access
        assertThrows(AccessDeniedException.class, () -> {
            documentService.getDocument(doc.getId());
        });

        // 6. user2 must be denied WRITE access
        assertThrows(AccessDeniedException.class, () -> {
            documentService.updateDocument(doc.getId(), "Hacked by User 2");
        });
    }

    @Test
    void testControllerEndpointAclEnforcement() {
        // 1. Authenticate as bob and create a document
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
        Document doc = documentController.createDocument(new Document("Bob Financial Ledger"),
                SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(doc.getId());

        // 2. Bob can access via controller
        Document retrieved = documentController.getDocument(doc.getId());
        assertEquals("Bob Financial Ledger", retrieved.getContent());

        // 3. Authenticate as eve (unauthorized)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("eve", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );

        // 4. Eve is blocked with AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            documentController.getDocument(doc.getId());
        });

        assertThrows(AccessDeniedException.class, () -> {
            documentController.updateDocument(doc.getId(), new Document("Tampered by Eve"));
        });
    }
}

