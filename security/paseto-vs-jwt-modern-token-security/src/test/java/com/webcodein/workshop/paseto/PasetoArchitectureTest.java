package com.webcodein.workshop.paseto;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.paseto", importOptions = ImportOption.DoNotIncludeTests.class)
public class PasetoArchitectureTest {

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
    public static final ArchRule recordsInModelShouldBeRecords =
        classes().that().haveSimpleNameEndingWith("Claims")
            .or().haveSimpleNameEndingWith("Header")
            .or().haveSimpleNameEndingWith("Result")
            .should().beRecords();
}
