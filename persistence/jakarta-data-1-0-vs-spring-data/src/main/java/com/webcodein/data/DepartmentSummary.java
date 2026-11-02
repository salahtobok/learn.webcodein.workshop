package com.webcodein.data;

/**
 * Immutable DTO projection representing aggregated department statistics.
 *
 * @param department     Department name
 * @param employeeCount  Total number of employees in the department
 * @param averageSalary  Average employee salary in the department
 */
public record DepartmentSummary(String department, long employeeCount, double averageSalary) {
    public DepartmentSummary {
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department name cannot be null or blank");
        }
        if (employeeCount < 0) {
            throw new IllegalArgumentException("Employee count cannot be negative");
        }
        if (averageSalary < 0) {
            throw new IllegalArgumentException("Average salary cannot be negative");
        }
    }
}
