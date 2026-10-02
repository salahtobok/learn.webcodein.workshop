package com.webcodein.workshop.ai.agent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CustomerSupportAgent {

    private final ChatClient chatClient;

    public CustomerSupportAgent(ChatClient.Builder chatClientBuilder, com.webcodein.workshop.ai.agent.tool.OrderTools orderTools) {
        // We configure the chat client to have a system prompt and access to specific tools
        this.chatClient = chatClientBuilder
                .defaultSystem("You are a helpful customer support agent for WebCodein eCommerce. " +
                        "You can help customers check their order status and cancel orders if needed. " +
                        "Always be polite and concise. If an order ID is missing, ask the user for it.")
                .defaultTools(orderTools)
                .build();
    }

    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
