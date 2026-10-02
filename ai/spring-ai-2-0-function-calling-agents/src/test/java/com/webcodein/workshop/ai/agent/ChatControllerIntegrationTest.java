package com.webcodein.workshop.ai.agent;

import com.webcodein.workshop.ai.agent.controller.ChatController;
import com.webcodein.workshop.ai.agent.service.CustomerSupportAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class ChatControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private ChatController chatController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    public void contextLoads() {
        // Just verify context starts up successfully
    }
}
