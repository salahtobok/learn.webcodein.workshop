package com.webcodein.workshop.realtime;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.realtime", importOptions = ImportOption.DoNotIncludeTests.class)
public class RealTimeArchitectureTest {

    @ArchTest
    public static final ArchRule protocols_must_implement_interface =
            classes().that().resideInAPackage("..protocol..")
                    .and().haveSimpleNameEndingWith("Protocol")
                    .and().areNotInterfaces()
                    .should().implement(com.webcodein.workshop.realtime.protocol.RealTimeProtocol.class);

    @ArchTest
    public static final ArchRule no_spring_dependencies =
            noClasses().should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "org.springframework.boot.."
            );

    @ArchTest
    public static final ArchRule model_records_must_not_depend_on_protocols =
            noClasses().that().resideInAPackage("..model..")
                    .should().dependOnClassesThat().resideInAPackage("..protocol..");
}
