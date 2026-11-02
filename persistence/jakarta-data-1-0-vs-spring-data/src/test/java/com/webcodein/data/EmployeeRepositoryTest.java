package com.webcodein.data;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeRepositoryTest {

    static SessionFactory sessionFactory;
    static StatelessSession session;
    static EmployeeRepository repository;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Product.class)
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:employee_repo_test;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .buildSessionFactory();
        session = sessionFactory.openStatelessSession();
        repository = new EmployeeRepository_(session);
    }

    @AfterAll
    static void tearDown() {
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @DisplayName("Insert employees and findById via CrudRepository contract")
    void testInsertAndFindById() {
        Employee emp = new Employee(100L, "Margaret", "Hamilton", "Apollo", 180_000, true);
        repository.insert(emp);

        var found = repository.findById(100L);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Margaret");
        assertThat(found.get().getLastName()).isEqualTo("Hamilton");
        assertThat(found.get().getDepartment()).isEqualTo("Apollo");
    }

    @Test
    @DisplayName("Parameter-based query with @Find and @By matching multiple attributes")
    void testActiveInDepartmentParameterQuery() {
        repository.insert(new Employee(101L, "Katherine", "Johnson", "Orbital", 160_000, true));
        repository.insert(new Employee(102L, "Dorothy", "Vaughan", "Orbital", 140_000, false));
        repository.insert(new Employee(103L, "Mary", "Jackson", "Aero", 150_000, true));

        List<Employee> activeOrbital = repository.activeInDepartment("Orbital", true);
        assertThat(activeOrbital).hasSize(1);
        assertThat(activeOrbital.get(0).getLastName()).isEqualTo("Johnson");

        List<Employee> inactiveOrbital = repository.activeInDepartment("Orbital", false);
        assertThat(inactiveOrbital).hasSize(1);
        assertThat(inactiveOrbital.get(0).getLastName()).isEqualTo("Vaughan");
    }

    @Test
    @DisplayName("Custom JPQL query with positional parameter and ordering")
    void testFindHighEarnersJpqlQuery() {
        repository.insert(new Employee(104L, "John", "Backus", "Compilers", 220_000, true));
        repository.insert(new Employee(105L, "Dennis", "Ritchie", "Systems", 250_000, true));
        repository.insert(new Employee(106L, "Ken", "Thompson", "Systems", 240_000, true));

        List<Employee> highEarners = repository.findHighEarners(230_000);
        assertThat(highEarners).hasSize(2);
        assertThat(highEarners).extracting(Employee::getLastName).containsExactly("Ritchie", "Thompson");
    }

    @Test
    @DisplayName("DTO projection query returning Java 25 record summaries")
    void testDepartmentSummaryProjection() {
        repository.insert(new Employee(107L, "Claude", "Shannon", "Information", 200_000, true));
        repository.insert(new Employee(108L, "Norbert", "Wiener", "Information", 100_000, true));

        List<DepartmentSummary> summaries = repository.summarizeDepartments();
        assertThat(summaries).isNotEmpty();

        DepartmentSummary infoSummary = summaries.stream()
                .filter(s -> "Information".equals(s.department()))
                .findFirst()
                .orElse(null);

        assertThat(infoSummary).isNotNull();
        assertThat(infoSummary.employeeCount()).isEqualTo(2L);
        assertThat(infoSummary.averageSalary()).isEqualTo(150_000.0);
    }
}
