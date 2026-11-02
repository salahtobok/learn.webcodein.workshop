package com.webcodein.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepartmentSummaryTest {

    @Test
    @DisplayName("Record component accessors and immutability")
    void testRecordComponentAccessorsAndImmutability() {
        DepartmentSummary summary = new DepartmentSummary("Engineering", 42L, 125_000.50);

        assertThat(summary.department()).isEqualTo("Engineering");
        assertThat(summary.employeeCount()).isEqualTo(42L);
        assertThat(summary.averageSalary()).isEqualTo(125_000.50);
        assertThat(summary.toString()).contains("Engineering", "42", "125000.5");
    }

    @Test
    @DisplayName("Record validation rejects null department and negative values")
    void testRecordValidationContracts() {
        assertThatThrownBy(() -> new DepartmentSummary(null, 10L, 80_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Department name cannot be null or blank");

        assertThatThrownBy(() -> new DepartmentSummary("   ", 10L, 80_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Department name cannot be null or blank");

        assertThatThrownBy(() -> new DepartmentSummary("Sales", -1L, 80_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee count cannot be negative");

        assertThatThrownBy(() -> new DepartmentSummary("Sales", 5L, -500.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Average salary cannot be negative");
    }
}
