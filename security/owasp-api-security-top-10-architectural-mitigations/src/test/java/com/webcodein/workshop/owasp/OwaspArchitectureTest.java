package com.webcodein.workshop.owasp;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.owasp", importOptions = ImportOption.DoNotIncludeTests.class)
public class OwaspArchitectureTest {

    @ArchTest
    public static final ArchRule modelClassesShouldNotDependOnEngineOrService =
        noClasses().that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAnyPackage("..engine..", "..service..");

    @ArchTest
    public static final ArchRule engineClassesShouldNotDependOnService =
        noClasses().that().resideInAPackage("..engine..")
            .should().dependOnClassesThat().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule servicesShouldResideInServicePackage =
        classes().that().haveSimpleNameEndingWith("Service")
            .should().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule modelsShouldBeRecords =
        classes().that().haveSimpleNameEndingWith("Record")
            .or().haveSimpleNameEndingWith("Request")
            .or().haveSimpleNameEndingWith("Decision")
            .or().haveSimpleNameEndingWith("Context")
            .should().beRecords();
}
