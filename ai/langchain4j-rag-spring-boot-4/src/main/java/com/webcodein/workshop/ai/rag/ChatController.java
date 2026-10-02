package com.webcodein.workshop.ai.rag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final EnterpriseCustomerSupport aiService;

    public ChatController(EnterpriseCustomerSupport aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam String query) {
        return aiService.answerUserQuery(query);
    }
}
