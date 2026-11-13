package com.webcodein.workshop.quic;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.quic", importOptions = ImportOption.DoNotIncludeTests.class)
public class QuicArchitectureTest {

    @ArchTest
    public static final ArchRule modelClassesShouldBeIndependentOfEngine =
        noClasses().that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAPackage("..engine..");

    @ArchTest
    public static final ArchRule engineClassesShouldBeIndependentOfService =
        noClasses().that().resideInAPackage("..engine..")
            .should().dependOnClassesThat().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule servicesShouldResideInServicePackage =
        classes().that().haveSimpleNameEndingWith("Service")
            .should().resideInAPackage("..service..");

    @ArchTest
    public static final ArchRule recordsInModelShouldBeRecords =
        classes().that().haveSimpleNameEndingWith("Frame")
            .or().haveSimpleNameEndingWith("Packet")
            .or().haveSimpleNameEndingWith("Result")
            .should().beRecords();
}
