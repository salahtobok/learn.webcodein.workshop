package com.webcodein.firmware.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.firmware", importOptions = ImportOption.DoNotIncludeTests.class)
class FirmwareArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_reside_in_controller_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_should_reside_in_service_package =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..")
                    .andShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule repositories_should_be_interfaces_and_reside_in_repository_package =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().resideInAPackage("..repository..")
                    .andShould().beAssignableTo(JpaRepository.class);

    @ArchTest
    static final ArchRule entities_should_reside_in_model_package =
            classes()
                    .that().haveSimpleName("Firmware")
                    .should().resideInAPackage("..model..")
                    .andShould().beAnnotatedWith(Entity.class);

    @ArchTest
    static final ArchRule controllers_should_not_depend_directly_on_repositories =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..");
}
