package com.webcodein.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private ChatMemory chatMemory;

    private AiController aiController;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultAdvisors(any(Advisor[].class)).build()).thenReturn(chatClient);
        aiController = new AiController(chatClientBuilder, chatMemory);
    }

    @Test
    @DisplayName("chat endpoint invokes ChatClient and returns AI response content")
    void shouldReturnChatResponse() {
        when(chatClient.prompt().user("Hello").advisors(any(Consumer.class)).call().content())
                .thenReturn("Hello! How can I help you with Spring AI today?");

        String response = aiController.chat("Hello", "session-42");

        assertThat(response).isEqualTo("Hello! How can I help you with Spring AI today?");
    }

    @Test
    @DisplayName("weather endpoint registers WeatherService tool and returns weather info")
    void shouldReturnWeatherInfoUsingTools() {
        when(chatClient.prompt().user("What is the weather like in London?").tools(eq(WeatherService.class)).call().content())
                .thenReturn("The weather in London is Sunny with 24°C.");

        String response = aiController.weatherInfo("London");

        assertThat(response).contains("London").contains("Sunny");
    }

    @Test
    @DisplayName("author endpoint deserializes AI response into typed Author record")
    void shouldReturnAuthorInfoEntity() {
        AiController.Author mockAuthor = new AiController.Author(
                "J.R.R. Tolkien",
                "English writer and philologist.",
                List.of("The Hobbit", "The Lord of the Rings")
        );

        when(chatClient.prompt().user(contains("J.R.R. Tolkien")).call().entity(AiController.Author.class))
                .thenReturn(mockAuthor);

        AiController.Author author = aiController.getAuthorInfo("J.R.R. Tolkien");

        assertThat(author).isNotNull();
        assertThat(author.name()).isEqualTo("J.R.R. Tolkien");
        assertThat(author.famousBooks()).contains("The Hobbit", "The Lord of the Rings");
    }

    @Test
    @DisplayName("Author record maintains proper equality and component accessors")
    void shouldValidateAuthorRecord() {
        AiController.Author author1 = new AiController.Author("Isaac Asimov", "Biochemist and author", List.of("Foundation", "I, Robot"));
        AiController.Author author2 = new AiController.Author("Isaac Asimov", "Biochemist and author", List.of("Foundation", "I, Robot"));

        assertThat(author1).isEqualTo(author2);
        assertThat(author1.hashCode()).isEqualTo(author2.hashCode());
        assertThat(author1.name()).isEqualTo("Isaac Asimov");
        assertThat(author1.famousBooks()).hasSize(2);
    }
}
