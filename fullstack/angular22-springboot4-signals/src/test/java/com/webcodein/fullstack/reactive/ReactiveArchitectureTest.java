package com.webcodein.fullstack.reactive;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.fullstack.reactive", importOptions = ImportOption.DoNotIncludeTests.class)
public class ReactiveArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .andShould().resideInAPackage("..controller..")
                    .as("REST endpoints must be annotated with @RestController and reside in controller package");

    @ArchTest
    static final ArchRule servicesShouldBeAnnotatedWithService =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .andShould().resideInAPackage("..service..")
                    .as("Domain services must be annotated with @Service and reside in service package");

    @ArchTest
    static final ArchRule repositoriesShouldBeInterfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().resideInAPackage("..repository..")
                    .as("Reactive R2DBC repositories must be interfaces in repository package");

    @ArchTest
    static final ArchRule controllersShouldNotDependDirectlyOnRepositories =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .as("Controllers must access data exclusively through services, not directly via repositories");
}
