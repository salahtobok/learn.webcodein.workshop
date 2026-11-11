package com.webcodein.taskmanager;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.webcodein.taskmanager", importOptions = {ImportOption.DoNotIncludeTests.class})
public class DddArchitectureTest {

    @ArchTest
    static final ArchRule layered_architecture_respected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("com.webcodein.taskmanager.domain..")
            .layer("Application").definedBy("com.webcodein.taskmanager.application..")
            .layer("Infrastructure").definedBy("com.webcodein.taskmanager.infrastructure..")
            .layer("Presentation").definedBy("com.webcodein.taskmanager.presentation..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation")
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_application_or_infrastructure = classes()
            .that().resideInAPackage("com.webcodein.taskmanager.domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "com.webcodein.taskmanager.domain..",
                    "java..",
                    "jakarta.persistence.."
            );

    @ArchTest
    static final ArchRule application_services_should_be_annotated_with_service = classes()
            .that().resideInAPackage("com.webcodein.taskmanager.application..")
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule presentation_controllers_should_be_annotated_with_rest_controller = classes()
            .that().resideInAPackage("com.webcodein.taskmanager.presentation..")
            .and().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(RestController.class);
}
