package com.webcodein.workshop.mcp;

import com.webcodein.workshop.mcp.controller.McpDiscoveryController;
import com.webcodein.workshop.mcp.service.McpRegistryService;
import com.webcodein.workshop.mcp.tool.EnterpriseMcpTools;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class McpDiscoveryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private McpRegistryService registryService;

    @Mock
    private EnterpriseMcpTools mcpTools;

    @InjectMocks
    private McpDiscoveryController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/mcp/health should return UP status")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/mcp/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.protocol").value("Model Context Protocol (MCP) 1.0"));
    }

    @Test
    @DisplayName("GET /api/mcp/tools should return registered tool definitions")
    void shouldReturnToolDefinitions() throws Exception {
        given(registryService.getRegisteredTools()).willReturn(List.of());

        mockMvc.perform(get("/api/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/mcp/execute should return error when tool name is missing")
    void shouldRejectMissingToolName() throws Exception {
        mockMvc.perform(post("/api/mcp/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Field 'tool' is required"));
    }
}
