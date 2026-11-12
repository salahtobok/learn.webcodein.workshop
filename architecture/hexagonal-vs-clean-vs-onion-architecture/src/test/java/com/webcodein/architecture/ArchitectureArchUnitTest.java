package com.webcodein.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.architecture", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureArchUnitTest {

    @ArchTest
    static final ArchRule hexagonal_domain_must_not_depend_on_adapters =
            noClasses().that().resideInAPackage("..hexagonal.domain..")
                    .should().dependOnClassesThat().resideInAPackage("..hexagonal.adapters..")
                    .because("Hexagonal domain core must remain decoupled from outside adapters.");

    @ArchTest
    static final ArchRule clean_entities_must_not_depend_on_outer_layers =
            noClasses().that().resideInAPackage("..clean.entities..")
                    .should().dependOnClassesThat().resideInAnyPackage("..clean.usecases..", "..clean.controllers..", "..clean.gateways..")
                    .because("Clean Architecture entities must not depend on outer use cases or delivery mechanisms.");

    @ArchTest
    static final ArchRule onion_domain_must_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage("..onion.domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..onion.infrastructure..", "..onion.presentation..")
                    .because("Onion domain models must not depend on infrastructure or presentation.");

    @ArchTest
    static final ArchRule hexagonal_ports_must_be_interfaces =
            classes().that().resideInAPackage("..hexagonal.ports..")
                    .should().beInterfaces()
                    .because("Hexagonal ports must define contract interfaces.");
}
