package com.webcodein.workshop.localai;

import com.webcodein.workshop.localai.model.DocumentAnalysisResult;
import com.webcodein.workshop.localai.service.DocumentComplianceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentComplianceServiceTest {

    private DocumentComplianceService complianceService;

    @BeforeEach
    void setUp() {
        complianceService = new DocumentComplianceService();
    }

    @Test
    @DisplayName("Clean document returns 100 compliance score and no flags")
    void shouldReturnCleanResultForSafeDocument() {
        DocumentAnalysisResult result = complianceService.auditDocument("DOC-101", "This is a public quarterly progress report with financial summaries.");

        assertThat(result.documentId()).isEqualTo("DOC-101");
        assertThat(result.complianceScore()).isEqualTo(100);
        assertThat(result.riskFlags()).isEmpty();
    }

    @Test
    @DisplayName("Document with secret keys triggers compliance flag and score reduction")
    void shouldDetectSensitiveKeywords() {
        DocumentAnalysisResult result = complianceService.auditDocument("DOC-102", "Confidential memo regarding employee SSN records and unencrypted data.");

        assertThat(result.complianceScore()).isLessThan(100);
        assertThat(result.riskFlags()).contains("SENSITIVE_DATA_DETECTED", "CREDENTIAL_EXPOSURE_RISK");
    }

    @Test
    @DisplayName("Empty document returns zero compliance score")
    void shouldFlagEmptyDocument() {
        DocumentAnalysisResult result = complianceService.auditDocument("DOC-103", "");

        assertThat(result.complianceScore()).isEqualTo(0);
        assertThat(result.riskFlags()).contains("EMPTY_CONTENT");
    }
}
