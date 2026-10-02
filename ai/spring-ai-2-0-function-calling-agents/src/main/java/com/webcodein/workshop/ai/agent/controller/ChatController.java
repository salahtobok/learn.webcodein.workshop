package com.webcodein.workshop.ai.agent.controller;

import com.webcodein.workshop.ai.agent.service.CustomerSupportAgent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support")
public class ChatController {

    private final CustomerSupportAgent agent;

    public ChatController(CustomerSupportAgent agent) {
        this.agent = agent;
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String response) {}

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = agent.chat(request.message());
        return new ChatResponse(answer);
    }
}
