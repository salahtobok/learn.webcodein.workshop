package com.webcodein.security.rls.controller;

import com.webcodein.security.rls.entity.Document;
import com.webcodein.security.rls.repository.DocumentRepository;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentController Slice Tests")
class DocumentControllerTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentController documentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    @DisplayName("GET /api/documents should return documents returned by repository")
    void shouldReturnAllDocuments() throws Exception {
        Document doc1 = new Document("Doc 1", "tenant_a");
        doc1.setId(1L);
        Document doc2 = new Document("Doc 2", "tenant_a");
        doc2.setId(2L);

        when(documentRepository.findAll()).thenReturn(List.of(doc1, doc2));

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Doc 1"))
                .andExpect(jsonPath("$[1].content").value("Doc 2"));

        verify(documentRepository).findAll();
    }

    @Test
    @DisplayName("POST /api/documents should assign tenant from principal and save")
    void shouldCreateDocument() throws Exception {
        Document savedDoc = new Document("New Doc", "tenant_gamma");
        savedDoc.setId(99L);

        when(documentRepository.save(any(Document.class))).thenReturn(savedDoc);

        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken("tenant_gamma", "n/a");

        mockMvc.perform(post("/api/documents")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"New Doc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.tenantId").value("tenant_gamma"))
                .andExpect(jsonPath("$.content").value("New Doc"));

        verify(documentRepository).save(any(Document.class));
    }
}
