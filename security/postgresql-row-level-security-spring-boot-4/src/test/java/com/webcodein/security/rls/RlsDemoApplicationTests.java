package com.webcodein.security.rls;

import com.webcodein.security.rls.entity.Document;
import com.webcodein.security.rls.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class RlsDemoApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
    }

    @Test
    @Transactional
    void testRowLevelSecurityIsolatesTenants() {
        // Authenticate as Tenant A
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("tenantA", "password")
        );
        documentRepository.save(new Document("Tenant A Secret 1", "tenantA"));
        documentRepository.save(new Document("Tenant A Secret 2", "tenantA"));

        // Authenticate as Tenant B
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("tenantB", "password")
        );
        documentRepository.save(new Document("Tenant B Secret 1", "tenantB"));

        // Now if Tenant A queries ALL documents, they should only see 2
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("tenantA", "password")
        );
        List<Document> tenantADocs = documentRepository.findAll();
        assertEquals(2, tenantADocs.size());

        // If Tenant B queries ALL documents, they should only see 1
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("tenantB", "password")
        );
        List<Document> tenantBDocs = documentRepository.findAll();
        assertEquals(1, tenantBDocs.size());
    }
}
