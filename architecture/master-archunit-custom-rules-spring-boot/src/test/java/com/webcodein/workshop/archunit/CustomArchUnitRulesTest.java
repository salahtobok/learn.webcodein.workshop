package com.webcodein.workshop.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.webcodein.workshop.archunit.annotation.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = ArchUnitCustomRulesApplication.class, importOptions = {ImportOption.DoNotIncludeTests.class})
public class CustomArchUnitRulesTest {

    // Rule 1: Declarative rule protecting custom @UseCase boundary from web dependencies
    @ArchTest
    public static final ArchRule useCasesShouldNotDependOnWebLayer =
            classes()
                    .that().areAnnotatedWith(UseCase.class)
                    .should().onlyDependOnClassesThat().resideOutsideOfPackage("..web..")
                    .because("Application use cases orchestrate business workflows and must never couple to HTTP web controllers or DTOs");

    // Rule 2: Repository isolation - only accessible by services or repositories
    @ArchTest
    public static final ArchRule repositoriesShouldOnlyBeAccessedByServices =
            classes()
                    .that().resideInAPackage("..repository..")
                    .and().haveSimpleNameEndingWith("Repository")
                    .should().onlyBeAccessed().byAnyPackage("..service..", "..repository..")
                    .because("Data access repositories must be encapsulated by domain or application use cases");

    // Rule 3: DTO leak prevention - domain classes must not depend on web representations
    @ArchTest
    public static final ArchRule dtosShouldNotLeakToDomainLayer =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..web..")
                    .because("Domain entities represent core enterprise logic and must remain unaware of transport-layer DTOs");

    // Rule 4: Domain framework independence - domain layer must not depend on Spring Framework
    @ArchTest
    public static final ArchRule domainShouldNotDependOnSpringFramework =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                    .because("Domain entities should remain framework-agnostic POJOs/records independent of Spring infrastructure");

    // Rule 5: Custom ArchCondition banning legacy java.util.Date across the entire project
    @ArchTest
    public static final ArchRule enforceJavaTimeOverJavaUtilDate =
            classes()
                    .should(new ArchCondition<JavaClass>("not use legacy java.util.Date") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            boolean usesDate = item.getDirectDependenciesFromSelf().stream()
                                    .anyMatch(dependency -> dependency.getTargetClass().isEquivalentTo(Date.class));
                            if (usesDate) {
                                String message = String.format("Class %s depends on legacy java.util.Date; migrate to java.time API",
                                        item.getName());
                                events.add(SimpleConditionEvent.violated(item, message));
                            }
                        }
                    })
                    .because("Modern Java applications require the thread-safe, immutable java.time API (JSR-310)");

    // Rule 6: Custom ArchCondition banning field injection (@Autowired on fields)
    @ArchTest
    public static final ArchRule noFieldInjectionWithAutowired =
            classes()
                    .that().resideInAnyPackage("..service..", "..web..")
                    .should(new ArchCondition<JavaClass>("not use field injection via @Autowired") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            for (JavaField field : item.getAllFields()) {
                                if (field.isAnnotatedWith(Autowired.class)) {
                                    String message = String.format("Field %s in %s uses @Autowired field injection; use constructor injection instead",
                                            field.getName(), item.getName());
                                    events.add(SimpleConditionEvent.violated(field, message));
                                }
                            }
                        }
                    })
                    .because("Constructor injection ensures immutability, safe testing, and prevents uninitialized dependencies");

    // Rule 7: Custom ArchCondition ensuring controllers never bypass services to call repositories directly
    @ArchTest
    public static final ArchRule controllersMustNotDependOnRepositoriesDirectly =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should(new ArchCondition<JavaClass>("not depend directly on repository interfaces") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            boolean hasDirectRepoDep = item.getDirectDependenciesFromSelf().stream()
                                    .anyMatch(dep -> dep.getTargetClass().getPackageName().contains(".repository"));
                            if (hasDirectRepoDep) {
                                String message = String.format("Controller %s directly accesses repository layer; delegate to @UseCase instead",
                                        item.getName());
                                events.add(SimpleConditionEvent.violated(item, message));
                            }
                        }
                    })
                    .because("Controllers must delegate to application use cases to maintain transactional integrity and separation of concerns");

    // Rule 8: Custom Method Rule ensuring @Transactional methods are declared public
    @ArchTest
    public static final ArchRule transactionalMethodsMustBePublic =
            methods()
                    .that().areAnnotatedWith(Transactional.class)
                    .should(new ArchCondition<JavaMethod>("be public to allow Spring AOP proxy interception") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                            if (!method.getModifiers().contains(JavaModifier.PUBLIC)) {
                                String message = String.format("Method %s in %s is annotated with @Transactional but is not public",
                                        method.getName(), method.getOwner().getName());
                                events.add(SimpleConditionEvent.violated(method, message));
                            }
                        }
                    })
                    .because("Standard Spring CGLIB / JDK dynamic proxies cannot intercept non-public @Transactional methods");

    // Rule 9: Custom Predicate & Condition ensuring Web DTOs ending with Request or Response are Java records
    @ArchTest
    public static final ArchRule dtosMustBeJavaRecords =
            classes()
                    .that(new DescribedPredicate<JavaClass>("are web transport DTOs") {
                        @Override
                        public boolean test(JavaClass javaClass) {
                            return javaClass.getPackageName().contains(".web") &&
                                    (javaClass.getSimpleName().endsWith("Request") || javaClass.getSimpleName().endsWith("Response"));
                        }
                    })
                    .should(new ArchCondition<JavaClass>("be defined as immutable Java records") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            if (!item.isRecord()) {
                                String message = String.format("DTO class %s in web package should be an immutable Java record",
                                        item.getName());
                                events.add(SimpleConditionEvent.violated(item, message));
                            }
                        }
                    })
                    .because("HTTP request and response payloads must be immutable, transparent data carriers");
}
