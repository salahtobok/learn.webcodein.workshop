package com.webcodein.data;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.data.repository.Repository;
import jakarta.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.data", importOptions = {ImportOption.DoNotIncludeTests.class})
public class JakartaDataArchitectureTest {

    @ArchTest
    static final ArchRule repositoriesMustBeAnnotatedWithRepository =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .and().haveSimpleNameNotStartingWith("_")
                    .should().beAnnotatedWith(Repository.class)
                    .because("Jakarta Data repositories must be annotated with @Repository for code generation");

    @ArchTest
    static final ArchRule repositoriesMustBeInterfaces =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .and().haveSimpleNameNotStartingWith("_")
                    .should().beInterfaces()
                    .because("Jakarta Data repositories are declarative interface contracts");

    @ArchTest
    static final ArchRule entitiesMustBeAnnotatedWithEntity =
            classes().that().haveSimpleName("Employee")
                    .or().haveSimpleName("Product")
                    .should().beAnnotatedWith(Entity.class)
                    .because("Domain entities managed by Jakarta Data / Hibernate must be JPA entities");

    @ArchTest
    static final ArchRule recordProjectionsMustBeRecords =
            classes().that().haveSimpleNameEndingWith("Summary")
                    .should().beRecords()
                    .because("Jakarta Data DTO projections should be immutable Java Records");
}
