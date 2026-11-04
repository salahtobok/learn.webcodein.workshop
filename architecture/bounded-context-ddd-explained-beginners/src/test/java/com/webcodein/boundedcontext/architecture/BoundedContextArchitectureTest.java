package com.webcodein.boundedcontext.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
    packages = "com.webcodein.boundedcontext",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class BoundedContextArchitectureTest {

    @ArchTest
    static final ArchRule catalog_must_not_depend_on_order_context =
        noClasses()
            .that().resideInAPackage("..catalog..")
            .should().dependOnClassesThat().resideInAPackage("..order..")
            .as("Catalog context (Upstream) must never depend on the Order context (Downstream)");

    @ArchTest
    static final ArchRule order_domain_must_not_access_catalog_entity_directly =
        noClasses()
            .that().resideInAPackage("..order..")
            .and().resideOutsideOfPackage("..order.acl..")
            .should().dependOnClassesThat().haveFullyQualifiedName("com.webcodein.boundedcontext.catalog.Product")
            .as("Order context domain must never directly access the internal Catalog Product entity; all interaction must flow through the Anti-Corruption Layer (ACL)");

    @ArchTest
    static final ArchRule bounded_contexts_must_be_free_of_cycles =
        slices()
            .matching("com.webcodein.boundedcontext.(*)..")
            .should().beFreeOfCycles()
            .as("Bounded context packages must not have cyclical dependencies");
}
