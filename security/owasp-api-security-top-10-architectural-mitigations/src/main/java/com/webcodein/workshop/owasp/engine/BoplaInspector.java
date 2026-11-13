package com.webcodein.workshop.owasp.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.workshop.owasp.model.SecurityDecision;
import com.webcodein.workshop.owasp.model.UpdateAccountRequest;

import java.util.Iterator;
import java.util.Set;

/**
 * Architectural defense against OWASP API3:2023 - Broken Object Property Level Authorization (BOPLA / Mass Assignment).
 * Prevents attackers from injecting or modifying sensitive internal entity fields.
 */
public class BoplaInspector {

    private static final Set<String> ALLOWED_UPDATE_FIELDS = Set.of("email", "displayName");
    private static final Set<String> SENSITIVE_INTERNAL_FIELDS = Set.of("balance", "tier", "accountId", "tenantId", "ownerId", "role");

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Inspects raw JSON payload for forbidden property tampering attempts.
     */
    public SecurityDecision inspectRawJsonForMassAssignment(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            Iterator<String> fieldNames = root.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                if (SENSITIVE_INTERNAL_FIELDS.contains(field)) {
                    return SecurityDecision.deny(
                        "BOPLA_MASS_ASSIGNMENT_DETECTED",
                        "Tampering with internal property '" + field + "' is forbidden"
                    );
                }
                if (!ALLOWED_UPDATE_FIELDS.contains(field)) {
                    return SecurityDecision.deny(
                        "UNRECOGNIZED_PROPERTY",
                        "Property '" + field + "' is not permitted in update requests"
                    );
                }
            }
            return SecurityDecision.allow();
        } catch (Exception e) {
            return SecurityDecision.deny("MALFORMED_JSON", e.getMessage());
        }
    }

    /**
     * Safely parses JSON directly into an immutable, field-restricted DTO.
     */
    public UpdateAccountRequest parseStrictDto(String rawJson) throws Exception {
        SecurityDecision decision = inspectRawJsonForMassAssignment(rawJson);
        if (!decision.allowed()) {
            throw new SecurityException("BOPLA Violation: " + decision.reason());
        }
        return objectMapper.readValue(rawJson, UpdateAccountRequest.class);
    }
}
