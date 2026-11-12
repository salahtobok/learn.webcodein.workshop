package com.webcodein.flyway;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.flyway", importOptions = ImportOption.DoNotIncludeTests.class)
public class FlywayArchitectureTest {

    @ArchTest
    static final ArchRule repositories_should_be_interfaces =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule services_should_be_annotated_with_service =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

    @ArchTest
    static final ArchRule controllers_should_be_annotated_with_rest_controller =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class);

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repositories =
            noClasses().that().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static final ArchRule entities_should_be_annotated_with_entity =
            classes().that().haveSimpleName("Customer")
                    .should().beAnnotatedWith(jakarta.persistence.Entity.class);
}
