package com.webcodein.legacy;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

@AnalyzeClasses(packages = "com.webcodein.legacy", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // 1. Strict layered architecture (Frozen to allow existing legacy violations)
    @ArchTest
    static final ArchRule layerDependenciesAreRespected = FreezingArchRule.freeze(
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Controllers").definedBy("..user..")
                    .layer("Services").definedBy("..order..")
                    .layer("Utils").definedBy("..util..")
                    .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
                    .whereLayer("Utils").mayNotAccessAnyLayer()
    );

    // 2. Services should not depend on Controllers (Frozen)
    @ArchTest
    static final ArchRule servicesShouldNotDependOnControllers = FreezingArchRule.freeze(
            noClasses()
                    .that().resideInAPackage("..order..")
                    .should().dependOnClassesThat().resideInAPackage("..user..")
    );

    // 3. Architecture governance for new code: No generic exceptions
    @ArchTest
    static final ArchRule noGenericExceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    // 4. Modern time API enforcement: No java.util.Date or Calendar
    @ArchTest
    static final ArchRule noLegacyDateTimeApi =
            noClasses()
                    .should().dependOnClassesThat().haveFullyQualifiedName("java.util.Date")
                    .orShould().dependOnClassesThat().haveFullyQualifiedName("java.util.Calendar");
}
