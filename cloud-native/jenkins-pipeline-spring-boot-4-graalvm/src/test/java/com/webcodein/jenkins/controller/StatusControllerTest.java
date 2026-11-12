package com.webcodein.jenkins.controller;

import com.webcodein.jenkins.model.BuildPipelineStatus;
import com.webcodein.jenkins.service.PipelineStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
@AutoConfigureMockMvc
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PipelineStatusService pipelineStatusService;

    @Test
    @DisplayName("GET /api/status returns HTTP 200 with pipeline payload")
    void shouldReturnPipelineStatusPayload() throws Exception {
        BuildPipelineStatus mockStatus = new BuildPipelineStatus(
                "jenkins-pipeline-spring-boot-4-graalvm",
                "UP",
                "Jenkins Declarative Pipeline",
                "25+36-LTS",
                "Linux x86_64 AOT Native Executable",
                Instant.parse("2026-11-12T00:00:00Z")
        );

        given(pipelineStatusService.getCurrentStatus()).willReturn(mockStatus);

        mockMvc.perform(get("/api/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.application").value("jenkins-pipeline-spring-boot-4-graalvm"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.pipelineEngine").value("Jenkins Declarative Pipeline"))
                .andExpect(jsonPath("$.jvmRuntime").value("25+36-LTS"))
                .andExpect(jsonPath("$.graalvmNativeTarget").value("Linux x86_64 AOT Native Executable"))
                .andExpect(jsonPath("$.checkedAt").value("2026-11-12T00:00:00Z"));
    }
}
