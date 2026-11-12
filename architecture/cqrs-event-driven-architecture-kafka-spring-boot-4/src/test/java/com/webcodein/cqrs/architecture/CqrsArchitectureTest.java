package com.webcodein.cqrs.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.webcodein.cqrs.OrderCqrsApplication;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = OrderCqrsApplication.class, importOptions = {ImportOption.DoNotIncludeTests.class})
public class CqrsArchitectureTest {

    // Rule 1: Command (Write) model must never depend on Query (Read) model
    @ArchTest
    public static final ArchRule commandShouldNotDependOnQuery =
            noClasses()
                    .that().resideInAPackage("..command..")
                    .should().dependOnClassesThat().resideInAPackage("..query..")
                    .because("CQRS strictly mandates that write operations never depend on read-optimized views or query models");

    // Rule 2: Query (Read) model must never depend on Command (Write) model
    @ArchTest
    public static final ArchRule queryShouldNotDependOnCommand =
            noClasses()
                    .that().resideInAPackage("..query..")
                    .should().dependOnClassesThat().resideInAPackage("..command..")
                    .because("CQRS read models must be autonomous, projection-driven projections with zero coupling to write aggregates");

    // Rule 3: Event models must be immutable Java records
    @ArchTest
    public static final ArchRule eventsShouldBeRecords =
            classes()
                    .that().resideInAPackage("..event..")
                    .should(new ArchCondition<JavaClass>("be immutable Java records") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            if (!item.isRecord()) {
                                String message = String.format("Domain event %s must be an immutable Java record", item.getName());
                                events.add(SimpleConditionEvent.violated(item, message));
                            }
                        }
                    })
                    .because("Domain events published to Kafka represent immutable historic facts");

    // Rule 4: REST Controllers must not depend on other REST Controllers
    @ArchTest
    public static final ArchRule controllersMustNotCrossDepend =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
                    .because("REST controllers must remain isolated HTTP entry points delegating to application services");

    // Rule 5: Repositories should only be accessed by service or repository classes
    @ArchTest
    public static final ArchRule repositoriesShouldOnlyBeAccessedByServices =
            classes()
                    .that().resideInAnyPackage("..command..", "..query..")
                    .and().haveSimpleNameEndingWith("Repository")
                    .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..command..", "..query..")
                    .because("Data access repositories must be encapsulated within their respective command or query boundaries");
}
