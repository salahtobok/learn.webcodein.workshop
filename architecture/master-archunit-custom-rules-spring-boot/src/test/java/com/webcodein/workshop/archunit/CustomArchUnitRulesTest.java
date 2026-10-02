package com.webcodein.workshop.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.webcodein.workshop.archunit.annotation.UseCase;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CustomArchUnitRulesTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.webcodein.workshop.archunit");

    @Test
    void useCasesShouldNotDependOnWebLayer() {
        classes()
                .that().areAnnotatedWith(UseCase.class)
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("..web..")
                .check(importedClasses);
    }

    @Test
    void repositoriesShouldOnlyBeAccessedByServices() {
        classes()
                .that().resideInAPackage("..repository..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().onlyBeAccessed().byAnyPackage("..service..", "..repository..")
                .check(importedClasses);
    }

    @Test
    void dtosShouldNotLeakToDomainLayer() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..web..")
                .check(importedClasses);
    }

    @Test
    void enforceJavaTimeOverJavaUtilDate() {
        ArchCondition<com.tngtech.archunit.core.domain.JavaClass> notUseJavaUtilDate = 
                new ArchCondition<>("not use java.util.Date") {
            @Override
            public void check(com.tngtech.archunit.core.domain.JavaClass item, ConditionEvents events) {
                boolean usesDate = item.getDependenciesFromSelf().stream()
                        .anyMatch(dependency -> dependency.getTargetClass().isEquivalentTo(Date.class));
                if (usesDate) {
                    events.add(SimpleConditionEvent.violated(item, "Class " + item.getName() + " uses java.util.Date"));
                }
            }
        };

        ArchRuleDefinition.classes()
                .should(notUseJavaUtilDate)
                .because("We use java.time API (e.g. LocalDateTime) instead of java.util.Date in modern Java")
                .check(importedClasses);
    }
}
