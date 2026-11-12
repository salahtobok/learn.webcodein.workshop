package com.webcodein.nativedemo;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.nativedemo", importOptions = ImportOption.DoNotIncludeTests.class)
public class NativeArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Web endpoints in native images must explicitly declare @RestController for AOT mapping");

    @ArchTest
    static final ArchRule runtimeHintsClassesShouldImplementRegistrar =
            classes()
                    .that().haveSimpleNameEndingWith("Hints")
                    .should().implement(RuntimeHintsRegistrar.class)
                    .as("Custom AOT reflection hints registrars must implement RuntimeHintsRegistrar");

    @ArchTest
    static final ArchRule controllersShouldNotDependOnAotRegistrars =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().implement(RuntimeHintsRegistrar.class)
                    .as("Controllers must remain decoupled from build-time AOT hint registrations");

    @ArchTest
    static final ArchRule dynamicClassesShouldBeConcrete =
            classes()
                    .that().haveSimpleNameEndingWith("DynamicClass")
                    .should().notBeInterfaces()
                    .as("Reflectively instantiated native components must be concrete classes");
}
