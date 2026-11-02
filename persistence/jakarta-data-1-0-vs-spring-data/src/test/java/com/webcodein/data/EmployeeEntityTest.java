package com.webcodein.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeEntityTest {

    @Test
    @DisplayName("Employee constructor, accessors, and setters contract")
    void testEmployeeConstructorAndAccessors() {
        Employee employee = new Employee(10L, "Ada", "Lovelace", "Computing", 150_000, true);

        assertThat(employee.getId()).isEqualTo(10L);
        assertThat(employee.getFirstName()).isEqualTo("Ada");
        assertThat(employee.getLastName()).isEqualTo("Lovelace");
        assertThat(employee.getDepartment()).isEqualTo("Computing");
        assertThat(employee.getSalary()).isEqualTo(150_000);
        assertThat(employee.isActive()).isTrue();

        employee.setSalary(175_000);
        employee.setActive(false);

        assertThat(employee.getSalary()).isEqualTo(175_000);
        assertThat(employee.isActive()).isFalse();
        assertThat(employee.toString()).contains("Lovelace", "Computing");
    }

    @Test
    @DisplayName("Employee equality and hashCode based on entity ID identity")
    void testEmployeeEqualityAndHashCode() {
        Employee emp1 = new Employee(1L, "Grace", "Hopper", "Navy", 120_000, true);
        Employee emp2 = new Employee(1L, "Grace", "Hopper", "Navy", 130_000, false);
        Employee emp3 = new Employee(2L, "Alan", "Turing", "Bletchley", 140_000, true);

        assertThat(emp1).isEqualTo(emp2);
        assertThat(emp1.hashCode()).isEqualTo(emp2.hashCode());
        assertThat(emp1).isNotEqualTo(emp3);
    }
}
