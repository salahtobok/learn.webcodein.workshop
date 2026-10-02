package com.webcodein.workshop.ai.rag;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
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

    @MockitoBean
    private ChatLanguageModel chatLanguageModel;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @MockitoBean
    private EmbeddingStore embeddingStore;

    @Test
    void contextLoads() {
    }
}
