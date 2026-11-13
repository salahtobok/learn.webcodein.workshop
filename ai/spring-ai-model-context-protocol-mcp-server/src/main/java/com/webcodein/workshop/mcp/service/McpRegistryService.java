package com.webcodein.workshop.mcp.service;

import com.webcodein.workshop.mcp.model.McpPromptDefinition;
import com.webcodein.workshop.mcp.model.McpResourceDefinition;
import com.webcodein.workshop.mcp.model.McpToolDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class McpRegistryService {

    private final List<McpToolDefinition> tools = new CopyOnWriteArrayList<>();
    private final List<McpResourceDefinition> resources = new CopyOnWriteArrayList<>();
    private final List<McpPromptDefinition> prompts = new CopyOnWriteArrayList<>();

    public McpRegistryService() {
        // Register Tools
        tools.add(new McpToolDefinition(
                "checkProductStock",
                "Queries current real-time stock levels and warehouse location for a given SKU",
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "sku", Map.of("type", "string", "description", "The unique product SKU code, e.g., SKU-MACBOOK-16")
                        ),
                        "required", List.of("sku")
                ),
                true
        ));

        tools.add(new McpToolDefinition(
                "lookupCustomerTier",
                "Fetches loyalty tier benefits, discount percentage, and VIP support eligibility for a customer ID",
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "customerId", Map.of("type", "string", "description", "The customer identifier, e.g., CUST-101")
                        ),
                        "required", List.of("customerId")
                ),
                true
        ));

        tools.add(new McpToolDefinition(
                "reserveWarehouseStock",
                "Reserves product stock quantity for checkout processing",
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "sku", Map.of("type", "string", "description", "Product SKU"),
                                "quantity", Map.of("type", "integer", "description", "Units to reserve")
                        ),
                        "required", List.of("sku", "quantity")
                ),
                false
        ));

        // Register Resources
        resources.add(new McpResourceDefinition(
                "mcp://enterprise/catalog/overview",
                "Enterprise Product Catalog Snapshot",
                "application/json",
                "Live read-only JSON summary of enterprise warehouses and inventory availability"
        ));

        resources.add(new McpResourceDefinition(
                "mcp://enterprise/policy/returns",
                "Corporate Return and Refund Policies",
                "text/markdown",
                "Standard operating procedure and customer loyalty return guidelines"
        ));

        // Register Prompts
        prompts.add(new McpPromptDefinition(
                "tierBasedGreeting",
                "Generates a personalized enterprise VIP customer service greeting based on customer loyalty tier",
                List.of("customerId", "customerName"),
                "Hello {{customerName}}! We appreciate your loyalty with us. Let me review your current enterprise benefits and assist you right away."
        ));
    }

    public List<McpToolDefinition> getRegisteredTools() {
        return List.copyOf(tools);
    }

    public List<McpResourceDefinition> getRegisteredResources() {
        return List.copyOf(resources);
    }

    public List<McpPromptDefinition> getRegisteredPrompts() {
        return List.copyOf(prompts);
    }
}
