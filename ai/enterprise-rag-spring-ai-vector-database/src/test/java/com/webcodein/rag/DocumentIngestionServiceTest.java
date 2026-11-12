package com.webcodein.rag;

import com.webcodein.rag.service.DocumentIngestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private DocumentIngestionService ingestionService;

    @Test
    @DisplayName("Should ingest document, split into tokens, and add to VectorStore")
    void shouldIngestDocumentAndAddToVectorStore() {
        Resource resource = new ByteArrayResource("Enterprise RAG documentation text content for indexing.".getBytes()) {
            @Override
            public String getFilename() {
                return "enterprise-policy.txt";
            }
        };

        ingestionService.ingestDocument(resource);

        verify(vectorStore).add(anyList());
    }
}
