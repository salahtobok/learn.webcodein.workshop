package com.webcodein.workshop.ai.rag;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface EnterpriseCustomerSupport {

    @SystemMessage({
        "You are a helpful customer support assistant for a software company.",
        "Use the provided context to answer the user's question.",
        "If you do not know the answer based on the context, say 'I do not have enough information to answer that'."
    })
    String answerUserQuery(@UserMessage String userQuestion);
}
