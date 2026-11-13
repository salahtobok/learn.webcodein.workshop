package com.webcodein.workshop.csp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CspViolationReport(
        @JsonProperty("document-uri") String documentUri,
        @JsonProperty("referrer") String referrer,
        @JsonProperty("violated-directive") String violatedDirective,
        @JsonProperty("effective-directive") String effectiveDirective,
        @JsonProperty("original-policy") String originalPolicy,
        @JsonProperty("blocked-uri") String blockedUri,
        @JsonProperty("status-code") int statusCode
) {
    public CspViolationReport {
        if (violatedDirective == null && effectiveDirective == null) {
            throw new IllegalArgumentException("Either violatedDirective or effectiveDirective must be specified");
        }
    }
}
