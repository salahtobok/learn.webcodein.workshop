package com.webcodein.workshop.localai.model;

import java.util.List;
import java.util.Objects;

public record DocumentAnalysisResult(
        String documentId,
        int complianceScore,
        List<String> riskFlags,
        String summary
) {
    public DocumentAnalysisResult {
        Objects.requireNonNull(documentId, "Document ID must not be null");
        Objects.requireNonNull(riskFlags, "Risk flags must not be null");
        Objects.requireNonNull(summary, "Summary must not be null");
        if (complianceScore < 0 || complianceScore > 100) {
            throw new IllegalArgumentException("Compliance score must be between 0 and 100");
        }
    }

    public static DocumentAnalysisResult compliant(String documentId, String summary) {
        return new DocumentAnalysisResult(documentId, 100, List.of(), summary);
    }
}
