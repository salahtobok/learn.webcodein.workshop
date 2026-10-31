package com.webcodein.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringAiApplicationTests {

    @Autowired(required = false)
    private ChatMemory chatMemory;

    @Test
    @DisplayName("Application context loads successfully with ChatMemory bean")
    void contextLoads() {
        assertThat(chatMemory).isNotNull();
    }

    @Test
    @DisplayName("ChatMemory bean records and retrieves conversational messages")
    void shouldStoreAndRetrieveMessagesInChatMemory() {
        String conversationId = "test-session-123";
        UserMessage message = new UserMessage("Hello, Spring AI 2.0!");

        chatMemory.add(conversationId, List.of(message));
        var retrieved = chatMemory.get(conversationId);

        assertThat(retrieved).isNotEmpty();
        assertThat(retrieved.getFirst().getText()).isEqualTo("Hello, Spring AI 2.0!");
    }
}
