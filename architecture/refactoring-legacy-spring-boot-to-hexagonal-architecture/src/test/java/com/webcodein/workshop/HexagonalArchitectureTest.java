package com.webcodein.workshop;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.refactored", importOptions = ImportOption.DoNotIncludeTests.class)
public class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domainMustNotDependOnFrameworks =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "com.fasterxml.jackson..")
                    .as("Domain classes must not depend on technical framework libraries");

    @ArchTest
    static final ArchRule domainMustNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..adapter..")
                    .as("Domain classes must not depend on inbound or outbound adapters");

    @ArchTest
    static final ArchRule adaptersMustNotDependOnEachOther =
            noClasses()
                    .that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..adapter.out..")
                    .as("Inbound driving adapters must not depend on outbound driven adapters");

    @ArchTest
    static final ArchRule portsMustBeInterfaces =
            classes()
                    .that().resideInAPackage("..domain.port..")
                    .should().beInterfaces()
                    .as("Domain ports must be interfaces");
}
