package com.webcodein.rag;

import com.webcodein.rag.controller.RagController;
import com.webcodein.rag.service.ChatService;
import com.webcodein.rag.service.DocumentIngestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class EnterpriseRagApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pgvector = new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"));

    @Autowired
    private RagController ragController;

    @Autowired
    private DocumentIngestionService ingestionService;

    @Autowired
    private ChatService chatService;

    @Test
    @DisplayName("Verify Spring application context loads and all RAG beans are initialized with PgVector")
    void contextLoadsAndBeansAreInitialized() {
        assertThat(ragController).isNotNull();
        assertThat(ingestionService).isNotNull();
        assertThat(chatService).isNotNull();
    }
}
