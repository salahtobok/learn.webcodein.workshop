package com.webcodein.dop;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.webcodein.dop",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class DopArchitectureTest {

    @ArchTest
    static final ArchRule models_should_be_records_or_interfaces =
            classes()
                    .that().resideInAPackage("..model..")
                    .should().beRecords()
                    .orShould().beInterfaces();

    @ArchTest
    static final ArchRule models_should_not_depend_on_services_or_controllers =
            noClasses()
                    .that().resideInAPackage("..model..")
                    .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule services_must_be_annotated_with_service =
            classes()
                    .that().resideInAPackage("..service..")
                    .and().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule controllers_must_have_rest_controller_annotation =
            classes()
                    .that().resideInAPackage("..controller..")
                    .and().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .orShould().beAnnotatedWith(Controller.class);

    @ArchTest
    static final ArchRule no_classes_should_throw_generic_exceptions =
            GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
}
