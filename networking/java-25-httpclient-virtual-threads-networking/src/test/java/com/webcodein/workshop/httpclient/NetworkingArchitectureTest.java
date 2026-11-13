package com.webcodein.workshop.httpclient;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.httpclient", importOptions = ImportOption.DoNotIncludeTests.class)
public class NetworkingArchitectureTest {

    @ArchTest
    public static final ArchRule no_spring_dependencies =
            noClasses().should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "org.springframework.boot.."
            );

    @ArchTest
    public static final ArchRule model_must_not_depend_on_engine =
            noClasses().that().resideInAPackage("..model..")
                    .should().dependOnClassesThat().resideInAPackage("..engine..");

    @ArchTest
    public static final ArchRule records_should_reside_in_model =
            classes().that().areRecords()
                    .should().resideInAPackage("..model..");
}
