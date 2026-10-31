package com.webcodein.nativedemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return personalized greeting message")
    void shouldReturnGreeting() throws Exception {
        mockMvc.perform(get("/greet/Antigravity"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Antigravity! This response comes from a native image."));
    }
}
