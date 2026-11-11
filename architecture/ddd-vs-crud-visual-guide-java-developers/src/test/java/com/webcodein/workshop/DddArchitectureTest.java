package com.webcodein.workshop;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop", importOptions = {ImportOption.DoNotIncludeTests.class})
public class DddArchitectureTest {

    @ArchTest
    static final ArchRule domain_model_is_pure_java = classes()
            .that().resideInAPackage("com.webcodein.workshop.ddd.domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "com.webcodein.workshop.ddd.domain..",
                    "java.."
            );

    @ArchTest
    static final ArchRule crud_and_ddd_packages_are_strictly_isolated = noClasses()
            .that().resideInAPackage("com.webcodein.workshop.crud..")
            .should().dependOnClassesThat().resideInAPackage("com.webcodein.workshop.ddd..")
            .andShould().onlyHaveDependentClassesThat().resideInAnyPackage("com.webcodein.workshop.crud..", "com.webcodein.workshop.presentation..", "com.webcodein.workshop..");

    @ArchTest
    static final ArchRule application_services_should_be_annotated_with_service = classes()
            .that().resideInAPackage("..application..")
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule presentation_controllers_should_be_annotated_with_rest_controller = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(RestController.class);
}
