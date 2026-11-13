package com.webcodein.workshop.localai;

import com.webcodein.workshop.localai.controller.LocalAiController;
import com.webcodein.workshop.localai.model.DocumentAnalysisResult;
import com.webcodein.workshop.localai.model.InferenceRequest;
import com.webcodein.workshop.localai.model.InferenceResponse;
import com.webcodein.workshop.localai.service.DocumentComplianceService;
import com.webcodein.workshop.localai.service.LocalInferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LocalAiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocalInferenceService inferenceService;

    @Mock
    private DocumentComplianceService complianceService;

    @InjectMocks
    private LocalAiController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/local-ai/health should return UP status and local models")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/local-ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.backend").value("Ollama Local Daemon"));
    }

    @Test
    @DisplayName("POST /api/local-ai/generate should return generated text")
    void shouldReturnGeneratedResponse() throws Exception {
        given(inferenceService.executeLocalPrompt(any(InferenceRequest.class)))
                .willReturn(InferenceResponse.successful("Local AI generated answer", "deepseek-r1:8b", 45L));

        mockMvc.perform(post("/api/local-ai/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\": \"Explain Virtual Threads in Java 25\", \"model\": \"deepseek-r1:8b\", \"temperature\": 0.7}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Local AI generated answer"))
                .andExpect(jsonPath("$.model").value("deepseek-r1:8b"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/local-ai/analyze-document should return compliance evaluation")
    void shouldAuditDocument() throws Exception {
        given(complianceService.auditDocument(any(), any()))
                .willReturn(new DocumentAnalysisResult("DOC-01", 95, List.of(), "Valid"));

        mockMvc.perform(post("/api/local-ai/analyze-document")
                        .param("documentId", "DOC-01")
                        .content("Corporate meeting notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value("DOC-01"))
                .andExpect(jsonPath("$.complianceScore").value(95));
    }
}
