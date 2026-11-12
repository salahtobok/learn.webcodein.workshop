package com.webcodein.security.acl.controller;

import com.webcodein.security.acl.entity.Document;
import com.webcodein.security.acl.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentController Slice Tests")
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    @DisplayName("GET /api/documents/{id} should return document from service")
    void shouldReturnDocumentById() throws Exception {
        Document doc = new Document("Financial Audit 2026");
        doc.setId(10L);

        when(documentService.getDocument(10L)).thenReturn(doc);

        mockMvc.perform(get("/api/documents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.content").value("Financial Audit 2026"));

        verify(documentService).getDocument(10L);
    }

    @Test
    @DisplayName("POST /api/documents should create document with authenticated username")
    void shouldCreateDocument() throws Exception {
        Document createdDoc = new Document("New Strategic Plan");
        createdDoc.setId(42L);

        when(documentService.createDocument(any(Document.class), eq("alice"))).thenReturn(createdDoc);

        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken("alice", "password");

        mockMvc.perform(post("/api/documents")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"New Strategic Plan\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.content").value("New Strategic Plan"));

        verify(documentService).createDocument(any(Document.class), eq("alice"));
    }

    @Test
    @DisplayName("PUT /api/documents/{id} should update document content")
    void shouldUpdateDocument() throws Exception {
        Document updatedDoc = new Document("Updated Strategic Plan");
        updatedDoc.setId(42L);

        when(documentService.updateDocument(eq(42L), eq("Updated Strategic Plan"))).thenReturn(updatedDoc);

        mockMvc.perform(put("/api/documents/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Updated Strategic Plan\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.content").value("Updated Strategic Plan"));

        verify(documentService).updateDocument(eq(42L), eq("Updated Strategic Plan"));
    }
}
