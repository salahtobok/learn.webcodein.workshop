package com.webcodein.workshop.tls;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.tls", importOptions = {ImportOption.DoNotIncludeTests.class})
public class TlsArchitectureTest {

    @ArchTest
    public static final ArchRule noSpringDependencies = noClasses()
            .should().dependOnClassesThat().resideInAPackage("..org.springframework..")
            .because("The TLS 1.3 cryptography workshop must remain pure Java without framework dependencies");

    @ArchTest
    public static final ArchRule modelShouldNotDependOnEngine = noClasses()
            .that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAnyPackage("..engine..", "..crypto..")
            .because("Domain model records must remain decoupled from cryptographic implementation engines");

    @ArchTest
    public static final ArchRule cryptoClassesShouldBePackageContained = classes()
            .that().resideInAPackage("..crypto..")
            .should().onlyBeAccessed().byAnyPackage("..crypto..", "..engine..", "..tls..")
            .because("Cryptographic primitives should only be accessed by the engine and test suites");
}
