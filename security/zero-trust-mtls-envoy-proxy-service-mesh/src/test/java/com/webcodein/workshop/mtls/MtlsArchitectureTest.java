package com.webcodein.workshop.mtls;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.mtls", importOptions = ImportOption.DoNotIncludeTests.class)
public class MtlsArchitectureTest {

    @ArchTest
    public static final ArchRule modelClassesShouldNotDependOnEngineOrService =
        noClasses().that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAnyPackage("..engine..", "..service..");

    @ArchTest
    public static final ArchRule engineClassesShouldNotDependOnService =
        noClasses().that().resideInAPackage("..engine..")
            .should().dependOnClassesThat().resideInAnyPackage("..service..");

    @ArchTest
    public static final ArchRule servicesShouldResideInServicePackage =
        classes().that().haveSimpleNameEndingWith("Service")
            .should().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule modelsShouldBeRecords =
        classes().that().haveSimpleNameEndingWith("Id")
            .or().haveSimpleNameEndingWith("Identity")
            .or().haveSimpleNameEndingWith("Decision")
            .should().beRecords();
}
