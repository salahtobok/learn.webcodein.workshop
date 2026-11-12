package com.webcodein.flashing;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.flashing", importOptions = ImportOption.DoNotIncludeTests.class)
public class FlashArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldResideInControllerPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .as("Controllers must reside in the controller package");

    @ArchTest
    static final ArchRule entitiesShouldResideInModelPackage =
            classes()
                    .that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..model..")
                    .as("JPA Entities must reside in the model package");

    @ArchTest
    static final ArchRule repositoriesShouldBeInterfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .as("Repositories must be interfaces");

    @ArchTest
    static final ArchRule controllersShouldNotDependOnOtherControllers =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .as("Controllers should not depend on other controllers");
}
