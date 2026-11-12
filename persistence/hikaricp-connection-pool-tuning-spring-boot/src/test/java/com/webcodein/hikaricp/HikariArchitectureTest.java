package com.webcodein.hikaricp;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.hikaricp", importOptions = ImportOption.DoNotIncludeTests.class)
public class HikariArchitectureTest {

    // 1. Web Controllers must never access database repositories directly
    @ArchTest
    static final ArchRule controllers_must_not_access_repositories =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .because("Controllers must delegate database access through services.");

    // 2. Services should only be accessed by controllers or other services
    @ArchTest
    static final ArchRule services_should_only_be_accessed_by_controllers_or_services =
            classes().that().resideInAPackage("..service..")
                    .should().onlyHaveDependentClassesThat().resideInAnyPackage(
                            "..controller..",
                            "..service..",
                            "..HikariApplication"
                    );

    // 3. Prohibit field injection; enforce constructor injection
    @ArchTest
    static final ArchRule no_field_injection =
            GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    // 4. Controller naming convention
    @ArchTest
    static final ArchRule controllers_naming_convention =
            classes().that().resideInAPackage("..controller..")
                    .and().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().haveSimpleNameEndingWith("Controller");

    // 5. Service naming convention
    @ArchTest
    static final ArchRule services_naming_convention =
            classes().that().resideInAPackage("..service..")
                    .and().areAnnotatedWith(org.springframework.stereotype.Service.class)
                    .should().haveSimpleNameEndingWith("Service");

    // 6. Repository naming convention
    @ArchTest
    static final ArchRule repositories_naming_convention =
            classes().that().resideInAPackage("..repository..")
                    .should().haveSimpleNameEndingWith("Repository");
}
