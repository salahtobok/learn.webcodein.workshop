package com.webcodein.workshop.resilience.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.resilience", importOptions = ImportOption.DoNotIncludeTests.class)
public class ResilienceArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .as("REST Controllers must reside in the controller package");

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..service..")
                    .as("Services must reside in the service package");

    @ArchTest
    static final ArchRule controllers_must_not_access_external_gateways_directly =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Gateway")
                    .as("Controllers must communicate through Service layer, not external gateways");

    @ArchTest
    static final ArchRule domain_records_must_be_records =
            classes().that().resideInAPackage("..model..")
                    .should().beRecords()
                    .as("DTO and Domain model representations must be immutable records");
}
