package com.webcodein.cqrs;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.cqrs", importOptions = ImportOption.DoNotIncludeTests.class)
public class CqrsArchitectureTest {

    @ArchTest
    static final ArchRule commandServicesShouldResideInCommandPackage =
            classes().that().haveSimpleNameEndingWith("CommandService")
                    .should().resideInAPackage("..command..")
                    .andShould().beAnnotatedWith(Service.class)
                    .as("Command services must reside in the ..command.. package and be annotated with @Service");

    @ArchTest
    static final ArchRule projectorsShouldResideInQueryPackage =
            classes().that().haveSimpleNameEndingWith("Projector")
                    .should().resideInAPackage("..query..")
                    .andShould().beAnnotatedWith(Service.class)
                    .as("Event projectors must reside in the ..query.. package and be annotated with @Service");

    @ArchTest
    static final ArchRule commandServicesShouldNotDependOnQueryLayer =
            noClasses().that().haveSimpleNameEndingWith("CommandService")
                    .should().dependOnClassesThat().resideInAPackage("..query..")
                    .as("Command services must never depend on the query layer");

    @ArchTest
    static final ArchRule queryLayerShouldNotDependOnCommandServices =
            noClasses().that().resideInAPackage("..query..")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("CommandService")
                    .as("Query layer must never depend on command services");

    @ArchTest
    static final ArchRule eventsShouldResideInEventsPackage =
            classes().that().haveSimpleNameEndingWith("Event")
                    .should().resideInAPackage("..events..")
                    .as("Domain events must reside in the ..events.. package");
}
