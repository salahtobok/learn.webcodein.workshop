package com.webcodein.workshop.mcp.controller;

import com.webcodein.workshop.mcp.model.McpPromptDefinition;
import com.webcodein.workshop.mcp.model.McpResourceDefinition;
import com.webcodein.workshop.mcp.model.McpToolDefinition;
import com.webcodein.workshop.mcp.service.McpRegistryService;
import com.webcodein.workshop.mcp.tool.EnterpriseMcpTools;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mcp")
public class McpDiscoveryController {

    private final McpRegistryService registryService;
    private final EnterpriseMcpTools mcpTools;

    public McpDiscoveryController(McpRegistryService registryService, EnterpriseMcpTools mcpTools) {
        this.registryService = registryService;
        this.mcpTools = mcpTools;
    }

    @GetMapping("/tools")
    public ResponseEntity<List<McpToolDefinition>> listTools() {
        return ResponseEntity.ok(registryService.getRegisteredTools());
    }

    @GetMapping("/resources")
    public ResponseEntity<List<McpResourceDefinition>> listResources() {
        return ResponseEntity.ok(registryService.getRegisteredResources());
    }

    @GetMapping("/prompts")
    public ResponseEntity<List<McpPromptDefinition>> listPrompts() {
        return ResponseEntity.ok(registryService.getRegisteredPrompts());
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeTool(@RequestBody Map<String, Object> payload) {
        String toolName = (String) payload.get("tool");
        if (toolName == null || toolName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Field 'tool' is required"));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) payload.getOrDefault("arguments", Map.of());

        String result = switch (toolName) {
            case "checkProductStock" -> mcpTools.checkProductStock((String) arguments.get("sku"));
            case "lookupCustomerTier" -> mcpTools.lookupCustomerTier((String) arguments.get("customerId"));
            case "reserveWarehouseStock" -> {
                Number qty = (Number) arguments.getOrDefault("quantity", 0);
                yield mcpTools.reserveWarehouseStock((String) arguments.get("sku"), qty.intValue());
            }
            default -> "Error: Unknown MCP tool '" + toolName + "'.";
        };

        return ResponseEntity.ok(Map.of(
                "tool", toolName,
                "result", result,
                "status", "SUCCESS"
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "protocol", "Model Context Protocol (MCP) 1.0",
                "server", "Spring Boot 4.0.0 GA + Spring AI 2.0"
        ));
    }
}
