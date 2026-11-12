package com.webcodein.valhalla;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.valhalla", importOptions = ImportOption.DoNotIncludeTests.class)
public class ValhallaArchitectureTest {

    @ArchTest
    public static final ArchRule controllersShouldResideInControllerPackage = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..controller..")
            .andShould().haveSimpleNameEndingWith("Controller");

    @ArchTest
    public static final ArchRule repositoriesShouldResideInRepositoryPackage = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..repository..");

    @ArchTest
    public static final ArchRule controllersShouldNotDependOnOtherControllers = noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("Controllers must not depend on peer controller classes.");

    @ArchTest
    public static final ArchRule entitiesShouldResideInEntityPackage = classes()
            .that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().resideInAPackage("..entity..");

    @ArchTest
    public static final ArchRule repositoriesShouldNotDependOnWebControllers = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("Repository persistence layer must never depend on web presentation layer.");
}
