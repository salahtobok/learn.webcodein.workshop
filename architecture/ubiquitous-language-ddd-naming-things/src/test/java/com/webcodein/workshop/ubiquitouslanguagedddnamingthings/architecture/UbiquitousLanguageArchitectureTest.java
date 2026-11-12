package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.webcodein.workshop.ubiquitouslanguagedddnamingthings",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class UbiquitousLanguageArchitectureTest {

    @ArchTest
    public static final ArchRule domainClassesMustNotUseGenericTechnicalSuffixes =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().haveSimpleNameEndingWith("Manager")
                    .orShould().haveSimpleNameEndingWith("Helper")
                    .orShould().haveSimpleNameEndingWith("Processor")
                    .orShould().haveSimpleNameEndingWith("DataHolder")
                    .orShould().haveSimpleNameEndingWith("InfoObject")
                    .because("Ubiquitous language demands precise domain nouns instead of vague technical catch-all suffixes");

    @ArchTest
    public static final ArchRule domainMustNotDependOnPresentation =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..presentation..")
                    .because("Domain layer must remain independent of presentation layer");

    @ArchTest
    public static final ArchRule domainMustNotDependOnSpringWeb =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework.web..")
                    .because("Domain layer must remain clean of web framework dependencies");

    @ArchTest
    public static final ArchRule presentationMustNotBypassRepositories =
            classes()
                    .that().resideInAPackage("..presentation..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..presentation..",
                            "..domain..",
                            "java..",
                            "org.springframework..",
                            "jakarta.."
                    )
                    .because("Controllers should maintain clean layer boundaries");
}
