package com.webcodein.nplusone;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.webcodein.nplusone",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class HibernateArchitectureTest {

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule controllers_must_not_access_repositories_directly =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..");

    @ArchTest
    static final ArchRule entities_must_reside_in_domain_package =
            classes()
                    .that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..domain..");

    @ArchTest
    static final ArchRule repositories_must_be_interfaces_and_annotated =
            classes()
                    .that().resideInAPackage("..repository..")
                    .should().beInterfaces()
                    .andShould().beAnnotatedWith(Repository.class);

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
