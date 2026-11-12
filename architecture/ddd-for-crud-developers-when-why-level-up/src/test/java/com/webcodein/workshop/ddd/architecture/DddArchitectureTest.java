package com.webcodein.workshop.ddd.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.webcodein.workshop.ddd",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class DddArchitectureTest {

    @ArchTest
    public static final ArchRule domainMustNotDependOnPresentation =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..presentation..")
                    .because("Domain layer must remain independent of presentation details");

    @ArchTest
    public static final ArchRule domainMustNotDependOnApplication =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..application..")
                    .because("Domain layer must remain independent of application orchestration");

    @ArchTest
    public static final ArchRule domainMustNotDependOnSpringWeb =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework.web..")
                    .because("Domain classes must not be polluted with Spring Web dependencies");

    @ArchTest
    public static final ArchRule presentationMustNotAccessPersistenceDirectly =
            noClasses()
                    .that().resideInAPackage("..presentation..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence..")
                    .because("Controllers should interact with application services, not directly with persistence adapters");
}
