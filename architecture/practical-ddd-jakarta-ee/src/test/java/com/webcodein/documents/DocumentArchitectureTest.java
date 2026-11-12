package com.webcodein.documents;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.documents", importOptions = ImportOption.DoNotIncludeTests.class)
public class DocumentArchitectureTest {

    // 1. Domain layer must never depend on application, interfaces, or Spring Framework
    @ArchTest
    static final ArchRule domain_must_not_depend_on_outer_layers =
            noClasses().that().resideInAPackage("..domain.model..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..application..",
                            "..interfaces..",
                            "org.springframework.."
                    ).because("Domain models must remain decoupled from outer application and interface layers.");

    // 2. Interfaces (REST) must never depend directly on the domain repository (must go through service)
    @ArchTest
    static final ArchRule interfaces_must_not_access_repositories =
            noClasses().that().resideInAPackage("..interfaces..")
                    .should().dependOnClassesThat().resideInAPackage("..domain.repository..")
                    .because("Web interfaces must coordinate actions through application services.");

    // 3. Application services must reside in application.service and end with Service
    @ArchTest
    static final ArchRule services_naming_convention =
            classes().that().resideInAPackage("..application.service..")
                    .should().haveSimpleNameEndingWith("Service");

    // 4. Repositories must reside in domain.repository and end with Repository (ignoring generated classes like DocumentRepository_)
    @ArchTest
    static final ArchRule repositories_naming_convention =
            classes().that().resideInAPackage("..domain.repository..")
                    .and().haveSimpleNameNotEndingWith("_")
                    .should().haveSimpleNameEndingWith("Repository");

    // 5. REST resources must end with Resource
    @ArchTest
    static final ArchRule resources_naming_convention =
            classes().that().resideInAPackage("..interfaces.rest..")
                    .and().areAnnotatedWith(jakarta.ws.rs.Path.class)
                    .should().haveSimpleNameEndingWith("Resource");

    // 6. Prohibit field injection; enforce constructor injection
    @ArchTest
    static final ArchRule no_field_injection =
            GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
}
