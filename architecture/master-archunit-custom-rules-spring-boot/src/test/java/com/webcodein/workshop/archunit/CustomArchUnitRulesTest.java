package com.webcodein.workshop.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.webcodein.workshop.archunit.annotation.UseCase;

import java.util.Date;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = ArchUnitCustomRulesApplication.class, importOptions = {ImportOption.DoNotIncludeTests.class})
class CustomArchUnitRulesTest {

    @ArchTest
    static final ArchRule useCasesShouldNotDependOnWebLayer =
            classes()
                    .that().areAnnotatedWith(UseCase.class)
                    .should().onlyDependOnClassesThat().resideOutsideOfPackage("..web..");

    @ArchTest
    static final ArchRule repositoriesShouldOnlyBeAccessedByServices =
            classes()
                    .that().resideInAPackage("..repository..")
                    .and().haveSimpleNameEndingWith("Repository")
                    .should().onlyBeAccessed().byAnyPackage("..service..", "..repository..");

    @ArchTest
    static final ArchRule dtosShouldNotLeakToDomainLayer =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..web..");

    @ArchTest
    static final ArchRule enforceJavaTimeOverJavaUtilDate =
            ArchRuleDefinition.classes()
                    .should(new ArchCondition<JavaClass>("not use java.util.Date") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            boolean usesDate = item.getDirectDependenciesFromSelf().stream()
                                    .anyMatch(dependency -> dependency.getTargetClass().isEquivalentTo(Date.class));
                            if (usesDate) {
                                events.add(SimpleConditionEvent.violated(item, "Class " + item.getName() + " uses java.util.Date"));
                            }
                        }
                    })
                    .because("We use java.time API (e.g. LocalDateTime) instead of java.util.Date in modern Java");
}
