package com.webcodein.rag;

import com.webcodein.rag.controller.RagController;
import com.webcodein.rag.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RagControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private RagController ragController;

    @Test
    @DisplayName("Should delegate chat query to ChatService and return augmented response")
    void shouldDelegateQueryToChatService() {
        when(chatService.chat("What is the refund policy?")).thenReturn("Refunds are processed within 14 days.");

        String response = ragController.chat("What is the refund policy?");

        assertThat(response).isEqualTo("Refunds are processed within 14 days.");
        verify(chatService).chat("What is the refund policy?");
    }
}
