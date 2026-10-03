package com.webcodein.legacy;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.webcodein.legacy")
public class ArchitectureTest {

    // 1. Strict layered architecture (Frozen to allow legacy violations)
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
                    .allowEmptyShould(true)
    );
}
