package com.webcodein.workshop.ai.agent;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.ai.agent", importOptions = ImportOption.DoNotIncludeTests.class)
public class AgentArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Classes ending with Controller must be annotated with @RestController");

    @ArchTest
    static final ArchRule controllersShouldResideInControllerPackage =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .as("REST Controllers should reside in the ..controller.. package");

    @ArchTest
    static final ArchRule servicesShouldBeAnnotatedWithService =
            classes().that().haveSimpleNameEndingWith("Agent")
                    .should().beAnnotatedWith(Service.class)
                    .as("AI Agent services must be annotated with @Service");

    @ArchTest
    static final ArchRule servicesShouldResideInServicePackage =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..service..")
                    .as("Services should reside in the ..service.. package");

    @ArchTest
    static final ArchRule toolsShouldResideInToolPackage =
            classes().that().haveSimpleNameEndingWith("Tools")
                    .should().resideInAPackage("..tool..")
                    .andShould().beAnnotatedWith(Component.class)
                    .as("Tool classes should reside in the ..tool.. package and be annotated with @Component");
}
