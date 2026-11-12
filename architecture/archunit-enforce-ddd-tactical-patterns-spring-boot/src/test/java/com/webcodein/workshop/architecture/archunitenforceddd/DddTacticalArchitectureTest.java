package com.webcodein.workshop.architecture.archunitenforceddd;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.AggregateRoot;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.ValueObject;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.webcodein.workshop.architecture.archunitenforceddd", importOptions = ImportOption.DoNotIncludeTests.class)
public class DddTacticalArchitectureTest {

    @ArchTest
    public static final ArchRule repositories_must_only_return_aggregate_roots =
            classes()
                    .that().resideInAPackage("..domain.repository..")
                    .and().haveSimpleNameEndingWith("Repository")
                    .and().areInterfaces()
                    .should().dependOnClassesThat().areAnnotatedWith(AggregateRoot.class)
                    .because("Repositories must only manage Aggregate Roots in DDD.");

    @ArchTest
    public static final ArchRule value_objects_must_have_only_final_fields =
            classes()
                    .that().areAnnotatedWith(ValueObject.class)
                    .should().haveOnlyFinalFields()
                    .because("Value Objects must be completely immutable.");

    @ArchTest
    public static final ArchRule domain_must_not_depend_on_presentation =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..presentation..")
                    .because("Domain must never depend on the Presentation layer.");

    @ArchTest
    public static final ArchRule controllers_must_not_access_repositories_directly =
            noClasses()
                    .that().resideInAPackage("..presentation..")
                    .should().dependOnClassesThat().resideInAPackage("..domain.repository..")
                    .because("Controllers must interact with Application Services, never directly with Repositories.");

    @ArchTest
    public static final ArchRule services_must_reside_in_application_package =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..application.service..")
                    .because("Application services must reside in the application.service package.");

    @ArchTest
    public static final ArchRule layer_dependencies_are_respected =
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Presentation").definedBy("..presentation..")
                    .layer("Application").definedBy("..application..")
                    .layer("Domain").definedBy("..domain..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation")
                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();
}
