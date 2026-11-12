package com.webcodein.rag;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.rag", importOptions = ImportOption.DoNotIncludeTests.class)
public class RagArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Classes ending with Controller must be annotated with @RestController");

    @ArchTest
    static final ArchRule controllersShouldResideInControllerPackage =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .as("Controller classes must reside in the ..controller.. package");

    @ArchTest
    static final ArchRule servicesShouldBeAnnotatedWithService =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .as("Classes ending with Service must be annotated with @Service");

    @ArchTest
    static final ArchRule servicesShouldResideInServicePackage =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..")
                    .as("Service classes must reside in the ..service.. package");

    @ArchTest
    static final ArchRule servicesShouldNotDependOnControllers =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .as("Service layer must never depend on the controller layer");
}
