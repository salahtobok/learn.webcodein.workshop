package com.bookstore;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.bookstore", importOptions = ImportOption.DoNotIncludeTests.class)
public class DddArchitectureTest {

    @ArchTest
    public static final ArchRule domain_should_not_depend_on_application_or_infrastructure =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..");

    @ArchTest
    public static final ArchRule services_in_domain_should_be_domain_services =
            classes().that().resideInAPackage("..domain..")
                    .and().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

    @ArchTest
    public static final ArchRule repositories_in_domain_should_be_interfaces =
            classes().that().resideInAPackage("..domain..")
                    .and().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces();
}
