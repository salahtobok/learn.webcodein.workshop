package com.webcodein.ddd;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

@AnalyzeClasses(packages = "com.webcodein.ddd", importOptions = ImportOption.DoNotIncludeTests.class)
class DddArchitectureTest {

    @ArchTest
    static final ArchRule no_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    static final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule domain_layer_must_not_depend_on_application_or_web =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..application..", "..web..");

    @ArchTest
    static final ArchRule controllers_must_reside_in_web_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..web..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_reside_in_application_package =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..application..")
                    .andShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule no_legacy_date_time_api =
            noClasses()
                    .should().dependOnClassesThat().haveFullyQualifiedName("java.util.Date")
                    .orShould().dependOnClassesThat().haveFullyQualifiedName("java.util.Calendar");
}
