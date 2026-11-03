package com.webcodein.workshop.ai.rag;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RagApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("vector_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("langchain4j.pgvector.host", postgres::getHost);
        registry.add("langchain4j.pgvector.port", postgres::getFirstMappedPort);
        registry.add("langchain4j.pgvector.database", postgres::getDatabaseName);
        registry.add("langchain4j.pgvector.user", postgres::getUsername);
        registry.add("langchain4j.pgvector.password", postgres::getPassword);
        registry.add("langchain4j.pgvector.dimension", () -> 1536);
        registry.add("langchain4j.open-ai.chat-model.api-key", () -> "fake-key");
        registry.add("langchain4j.open-ai.embedding-model.api-key", () -> "fake-key");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatController chatController;

    @Autowired
    private DocumentIngestionService ingestionService;

    @Autowired
    private EnterpriseCustomerSupport customerSupport;

    @MockitoBean
    private ChatLanguageModel chatLanguageModel;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @MockitoBean
    private EmbeddingStore embeddingStore;

    @Test
    @DisplayName("Verify Spring application context loads and core RAG beans are properly autowired")
    void contextLoadsAndBeansAreInitialized() {
        assertThat(mockMvc).isNotNull();
        assertThat(chatController).isNotNull();
        assertThat(ingestionService).isNotNull();
        assertThat(customerSupport).isNotNull();
    }

    @Test
    @DisplayName("Verify /api/chat endpoint processes user queries via AiService declarative interface")
    void testChatEndpoint() throws Exception {
        when(embeddingModel.embed(anyString()))
                .thenReturn(Response.from(dev.langchain4j.data.embedding.Embedding.from(new float[1536])));
        when(embeddingStore.search(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new dev.langchain4j.store.embedding.EmbeddingSearchResult<>(List.of()));
        when(chatLanguageModel.generate(anyList()))
                .thenReturn(Response.from(AiMessage.from("Enterprise support response: Refunds are processed within 5 business days.")));

        mockMvc.perform(get("/api/chat").param("query", "What is the refund turnaround time?"))
                .andExpect(status().isOk())
                .andExpect(content().string("Enterprise support response: Refunds are processed within 5 business days."));
    }
}
