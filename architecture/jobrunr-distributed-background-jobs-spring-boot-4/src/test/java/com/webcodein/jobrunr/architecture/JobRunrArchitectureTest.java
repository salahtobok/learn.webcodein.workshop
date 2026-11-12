package com.webcodein.jobrunr.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.jobrunr", importOptions = ImportOption.DoNotIncludeTests.class)
public class JobRunrArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .as("Controllers should reside in the controller package");

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..")
                    .as("Services should reside in the service package");

    @ArchTest
    static final ArchRule controllers_must_be_annotated_with_rest_controller =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Controller classes must be annotated with @RestController");

    @ArchTest
    static final ArchRule services_must_be_annotated_with_service =
            classes()
                    .that().resideInAPackage("..service..")
                    .should().beAnnotatedWith(Service.class)
                    .as("Service classes must be annotated with @Service");

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .as("Service layer must not depend on controller layer");
}
