package com.webcodein.workshop.gateway.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.gateway", importOptions = ImportOption.DoNotIncludeTests.class)
public class GatewayArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..")
                    .andShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule filters_must_reside_in_filter_package =
            classes().that().haveSimpleNameEndingWith("Filter")
                    .should().resideInAPackage("..filter..")
                    .andShould().beAssignableTo(OncePerRequestFilter.class);

    @ArchTest
    static final ArchRule models_must_reside_in_model_package =
            classes().that().resideInAPackage("..model..")
                    .should().beRecords();
}
