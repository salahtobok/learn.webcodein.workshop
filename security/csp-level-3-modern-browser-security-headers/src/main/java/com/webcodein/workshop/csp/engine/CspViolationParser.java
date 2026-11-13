package com.webcodein.workshop.csp.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.workshop.csp.model.CspViolationReport;

import java.io.IOException;
import java.util.Objects;

public class CspViolationParser {

    private final ObjectMapper objectMapper;

    public CspViolationParser() {
        this.objectMapper = new ObjectMapper();
    }

    public CspViolationReport parseReport(String jsonPayload) throws IOException {
        Objects.requireNonNull(jsonPayload, "jsonPayload must not be null");

        JsonNode root = objectMapper.readTree(jsonPayload);
        JsonNode reportNode = root;

        // Legacy format wraps in {"csp-report": { ... }}
        if (root.has("csp-report")) {
            reportNode = root.get("csp-report");
        } else if (root.has("body")) {
            // W3C Reporting API format wraps in {"type": "csp-violation", "body": { ... }}
            reportNode = root.get("body");
        }

        return objectMapper.treeToValue(reportNode, CspViolationReport.class);
    }
}
