package com.webcodein.virtualthreads;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.virtualthreads", importOptions = ImportOption.DoNotIncludeTests.class)
public class VirtualThreadsArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldResideInApiPackage =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..api..")
                    .as("REST Controllers should reside in the ..api.. package");

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Classes ending with Controller should be annotated with @RestController");

    @ArchTest
    static final ArchRule servicesShouldResideInServicePackage =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..service..")
                    .as("Services should reside in the ..service.. package");

    @ArchTest
    static final ArchRule servicesShouldBeAnnotatedWithService =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .as("Classes ending with Service should be annotated with @Service");

    @ArchTest
    static final ArchRule servicesShouldNotDependOnControllers =
            classes().that().resideInAPackage("..service..")
                    .should().onlyHaveDependentClassesThat().resideInAnyPackage("..service..", "..api..", "..virtualthreads..")
                    .as("Service layer components must maintain clean layer separation");
}
