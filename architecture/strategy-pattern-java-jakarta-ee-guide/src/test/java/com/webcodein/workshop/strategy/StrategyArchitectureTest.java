package com.webcodein.workshop.strategy;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.webcodein.workshop.strategy.domain.PaymentStrategy;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.strategy", importOptions = ImportOption.DoNotIncludeTests.class)
class StrategyArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_presentation_and_be_annotated =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..presentation..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule concrete_strategies_must_implement_payment_strategy =
            classes()
                    .that().haveSimpleNameEndingWith("Strategy")
                    .and().areNotInterfaces()
                    .should().resideInAPackage("..infrastructure..")
                    .andShould().implement(PaymentStrategy.class);

    @ArchTest
    static final ArchRule processors_must_reside_in_application =
            classes()
                    .that().haveSimpleNameEndingWith("Processor")
                    .should().resideInAPackage("..application..");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_presentation_or_infrastructure =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..presentation..", "..infrastructure..");

    @ArchTest
    static final ArchRule no_classes_should_use_legacy_date =
            noClasses()
                    .should().dependOnClassesThat().haveFullyQualifiedName("java.util.Date")
                    .because("Modern Java 25 applications must use modern java.time types");
}
