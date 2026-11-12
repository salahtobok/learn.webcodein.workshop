package com.webcodein.cicd.controller;

import com.webcodein.cicd.model.DeploymentStatus;
import com.webcodein.cicd.service.DeploymentHealthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppController.class)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeploymentHealthService deploymentHealthService;

    @Test
    @DisplayName("GET /api/status should return UP status and version")
    void shouldReturnStatus() throws Exception {
        when(deploymentHealthService.getCurrentStatus())
                .thenReturn(DeploymentStatus.up("1.0.0", "swarm-node-1"));

        mockMvc.perform(get("/api/status").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.hostname").value("swarm-node-1"))
                .andExpect(jsonPath("$.message").value("Zero-downtime deployment successful!"));
    }

    @Test
    @DisplayName("GET /api/status/details should return full deployment status")
    void shouldReturnStatusDetails() throws Exception {
        when(deploymentHealthService.getCurrentStatus())
                .thenReturn(DeploymentStatus.up("2.0.0", "swarm-manager-01"));

        mockMvc.perform(get("/api/status/details").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.version").value("2.0.0"))
                .andExpect(jsonPath("$.hostname").value("swarm-manager-01"));
    }
}
