package com.webcodein.workshop.ratelimit;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.ratelimit", importOptions = ImportOption.DoNotIncludeTests.class)
class RateLimitingArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_be_annotated_with_rest_controller =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_be_annotated_with_service =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule controllers_reside_in_controller_package =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule services_reside_in_service_package =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..");
}
