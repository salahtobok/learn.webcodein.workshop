package com.webcodein.cicd;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.cicd", importOptions = ImportOption.DoNotIncludeTests.class)
public class CicdArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_be_annotated_with_rest_controller =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class);

    @ArchTest
    static final ArchRule services_should_be_annotated_with_service =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule models_should_not_depend_on_services_or_controllers =
            noClasses().that().resideInAPackage("..model..")
                    .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..");
}
