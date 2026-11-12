package com.webcodein.hexagonal;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.hexagonal", importOptions = ImportOption.DoNotIncludeTests.class)
public class HexagonalArchitectureTest {

    @ArchTest
    public static final ArchRule domain_should_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

    @ArchTest
    public static final ArchRule domain_model_should_not_depend_on_jpa_or_jaxrs =
            noClasses().that().resideInAPackage("..domain.model..")
                    .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "jakarta.ws.rs..", "org.springframework..");

    @ArchTest
    public static final ArchRule inbound_adapters_should_not_depend_on_outbound_adapters =
            noClasses().that().resideInAPackage("..infrastructure.adapter.in..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.adapter.out..");

    @ArchTest
    public static final ArchRule ports_should_be_interfaces =
            classes().that().resideInAPackage("..domain.port..")
                    .should().beInterfaces();
}
