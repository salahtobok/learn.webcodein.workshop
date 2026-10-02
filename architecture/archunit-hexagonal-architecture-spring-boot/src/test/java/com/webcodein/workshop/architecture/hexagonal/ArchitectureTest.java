package com.webcodein.workshop.architecture.hexagonal;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = "com.webcodein.workshop.architecture.hexagonal", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // 1. The Domain must be completely pure
    @ArchTest
    static final ArchRule domain_should_not_depend_on_outside =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application..",
                "..adapter..",
                "org.springframework.."
            );

    // 2. Adapters cannot call other Adapters
    @ArchTest
    static final ArchRule adapters_should_not_depend_on_each_other =
        noClasses().that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..");

    // 3. Application Services must only implement In-Ports (Use Cases) and use Out-Ports
    @ArchTest
    static final ArchRule application_should_only_depend_on_domain_and_ports =
        classes().that().resideInAPackage("..application.service..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..application.port..",
                "..application.service..",
                "..domain..",
                "java..",
                "org.springframework.stereotype.."
            );

    // 4. Using ArchUnit's built-in Onion Architecture check
    // Hexagonal is structurally identical to Onion in ArchUnit terms
    @ArchTest
    static final ArchRule hexagonal_architecture_is_respected =
        onionArchitecture()
            .domainModels("..domain..")
            .domainServices("..domain..")
            .applicationServices("..application..")
            .adapter("web", "..adapter.in.web..")
            .adapter("persistence", "..adapter.out.persistence..");

}
