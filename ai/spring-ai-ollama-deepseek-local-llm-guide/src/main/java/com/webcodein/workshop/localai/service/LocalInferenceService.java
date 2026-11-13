package com.webcodein.workshop.localai.service;

import com.webcodein.workshop.localai.model.InferenceRequest;
import com.webcodein.workshop.localai.model.InferenceResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LocalInferenceService {

    private final ChatClient chatClient;

    public LocalInferenceService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public InferenceResponse executeLocalPrompt(InferenceRequest request) {
        long start = System.currentTimeMillis();
        try {
            String answer = chatClient.prompt()
                    .user(request.prompt())
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - start;
            return InferenceResponse.successful(answer != null ? answer : "", request.model(), duration);
        } catch (Exception ex) {
            return InferenceResponse.failed(ex.getMessage(), request.model());
        }
    }
}
