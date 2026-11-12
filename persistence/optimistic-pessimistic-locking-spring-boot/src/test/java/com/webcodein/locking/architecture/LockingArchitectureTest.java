package com.webcodein.locking.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.webcodein.locking",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class LockingArchitectureTest {

    @ArchTest
    public static final ArchRule controllersMustNotAccessRepositoriesDirectly =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .because("Controllers should delegate transactional operations to services, not access repositories directly");

    @ArchTest
    public static final ArchRule servicesMustBeAnnotatedWithService =
            classes()
                    .that().resideInAPackage("..service..")
                    .and().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                    .because("Service layer components must be declared as Spring @Service beans");

    @ArchTest
    public static final ArchRule repositoriesMustBeInterfaces =
            classes()
                    .that().resideInAPackage("..repository..")
                    .should().beInterfaces()
                    .because("Spring Data repositories must be defined as interfaces");

    @ArchTest
    public static final ArchRule entitiesMustResideInEntityPackage =
            classes()
                    .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().resideInAPackage("..entity..")
                    .because("JPA entities must reside in the dedicated entity package");
}
