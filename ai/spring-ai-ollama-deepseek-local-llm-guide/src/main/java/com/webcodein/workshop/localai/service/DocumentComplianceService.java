package com.webcodein.workshop.localai.service;

import com.webcodein.workshop.localai.model.DocumentAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DocumentComplianceService {

    public DocumentAnalysisResult auditDocument(String documentId, String text) {
        if (text == null || text.isBlank()) {
            return new DocumentAnalysisResult(documentId, 0, List.of("EMPTY_CONTENT"), "Document contains no readable text.");
        }

        List<String> flags = new ArrayList<>();
        String lower = text.toLowerCase(Locale.ROOT);

        if (lower.contains("confidential") || lower.contains("ssn") || lower.contains("secret")) {
            flags.add("SENSITIVE_DATA_DETECTED");
        }
        if (lower.contains("unencrypted") || lower.contains("plaintext password")) {
            flags.add("CREDENTIAL_EXPOSURE_RISK");
        }

        int score = Math.max(0, 100 - (flags.size() * 30));
        String summary = flags.isEmpty()
                ? "Document passed local privacy verification with zero compliance violations."
                : "Compliance review flagged " + flags.size() + " issue(s) requiring local redaction.";

        return new DocumentAnalysisResult(documentId, score, flags, summary);
    }
}
