package com.webcodein.saas;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.saas", importOptions = ImportOption.DoNotIncludeTests.class)
public class SaasArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldResideInControllerPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .as("Controllers should reside in the controller package");

    @ArchTest
    static final ArchRule controllersShouldNotDependOnOtherControllers =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .as("Controllers should not depend on each other");

    @ArchTest
    static final ArchRule entitiesShouldResideInModelPackage =
            classes()
                    .that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..model..")
                    .as("JPA Entities should reside in the model package");

    @ArchTest
    static final ArchRule repositoriesShouldBeInterfaces =
            classes()
                    .that().resideInAPackage("..repository..")
                    .should().beInterfaces()
                    .as("Repositories should be interfaces");
}
