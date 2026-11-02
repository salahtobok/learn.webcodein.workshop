package com.webcodein.data;

import jakarta.data.repository.By;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    // Parameter-based query: each parameter is matched against the entity property named in @By
    @Find
    List<Employee> activeInDepartment(@By("department") String department, @By("active") boolean active);

    // Custom JPQL query with parameter binding
    @Query("SELECT e FROM Employee e WHERE e.salary > ?1 ORDER BY e.lastName ASC")
    List<Employee> findHighEarners(double minSalary);

    // DTO projection query returning Java 25 record
    @Query("SELECT new com.webcodein.data.DepartmentSummary(e.department, count(e), avg(e.salary)) FROM Employee e GROUP BY e.department ORDER BY e.department ASC")
    List<DepartmentSummary> summarizeDepartments();

    // Find by department
    @Find
    List<Employee> findByDepartment(@By("department") String department);
}
