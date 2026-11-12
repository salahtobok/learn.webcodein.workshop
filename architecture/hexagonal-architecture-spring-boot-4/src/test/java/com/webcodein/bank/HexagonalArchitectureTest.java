package com.webcodein.bank;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.bank", importOptions = ImportOption.DoNotIncludeTests.class)
public class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_adapters =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule domain_model_should_not_depend_on_spring =
            noClasses()
                    .that().resideInAPackage("..domain.model..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule inbound_adapters_should_not_depend_on_outbound_adapters =
            noClasses()
                    .that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule ports_should_be_interfaces =
            classes()
                    .that().resideInAPackage("..port..")
                    .should().beInterfaces();
}
