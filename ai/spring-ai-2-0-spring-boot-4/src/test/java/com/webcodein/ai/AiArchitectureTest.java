package com.webcodein.ai;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Modifier;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "com.webcodein.ai", importOptions = ImportOption.DoNotIncludeTests.class)
public class AiArchitectureTest {

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
    static final ArchRule restControllersShouldResideInAiPackage =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..ai..")
                    .as("REST Controllers should reside in the ..ai.. package");

    @ArchTest
    static final ArchRule toolMethodsShouldBePublic =
            methods().that().areAnnotatedWith(Tool.class)
                    .should(new ArchCondition<JavaMethod>("be public for reflection tool calling") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                            boolean isPublic = method.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.PUBLIC);
                            if (!isPublic) {
                                String message = String.format("Method %s is annotated with @Tool but is not public", method.getFullName());
                                events.add(SimpleConditionEvent.violated(method, message));
                            }
                        }
                    })
                    .as("@Tool annotated methods must be public for AI reflection execution");

    @ArchTest
    static final ArchRule recordsShouldBeRecords =
            classes().that().areRecords()
                    .should().notBeInterfaces()
                    .as("Record components should be concrete record types");
}
