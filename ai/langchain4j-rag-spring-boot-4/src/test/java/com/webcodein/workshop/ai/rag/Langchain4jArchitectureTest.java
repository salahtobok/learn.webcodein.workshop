package com.webcodein.workshop.ai.rag;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dev.langchain4j.service.spring.AiService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.ai.rag", importOptions = ImportOption.DoNotIncludeTests.class)
public class Langchain4jArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Classes ending with Controller must be annotated with @RestController");

    @ArchTest
    static final ArchRule servicesShouldBeAnnotatedWithService =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .as("Classes ending with Service must be annotated with @Service");

    @ArchTest
    static final ArchRule aiServicesShouldBeAnnotatedWithAiService =
            classes().that().haveSimpleNameEndingWith("CustomerSupport")
                    .should().beAnnotatedWith(AiService.class)
                    .andShould().beInterfaces()
                    .as("Declarative LangChain4j service interfaces must be annotated with @AiService");

    @ArchTest
    static final ArchRule servicesShouldNotDependOnControllers =
            noClasses().that().haveSimpleNameEndingWith("Service")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller")
                    .as("Service layer must never depend on the controller layer");

    @ArchTest
    static final ArchRule controllersShouldOnlyDependOnValidComponents =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().onlyAccessClassesThat().resideInAnyPackage(
                            "com.webcodein.workshop.ai.rag..",
                            "org.springframework..",
                            "java..",
                            "dev.langchain4j.."
                    )
                    .as("Controllers must only access application components, Spring, Java, and LangChain4j");
}
