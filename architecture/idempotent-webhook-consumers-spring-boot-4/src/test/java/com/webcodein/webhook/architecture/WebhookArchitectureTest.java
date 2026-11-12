package com.webcodein.webhook.architecture;

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

@AnalyzeClasses(packages = "com.webcodein.webhook", importOptions = ImportOption.DoNotIncludeTests.class)
public class WebhookArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .andShould().beAnnotatedWith(RestController.class)
                    .as("Controllers should reside in the controller package and be annotated with @RestController");

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes()
                    .that().haveSimpleNameEndingWith("Processor")
                    .should().resideInAPackage("..service..")
                    .andShould().beAnnotatedWith(Service.class)
                    .as("Processors should reside in service package and be annotated with @Service");

    @ArchTest
    static final ArchRule entities_must_reside_in_model_package =
            classes()
                    .that().haveSimpleName("WebhookEvent")
                    .should().resideInAPackage("..model..")
                    .andShould().beAnnotatedWith(Entity.class)
                    .as("Entities should reside in model package and be annotated with @Entity");

    @ArchTest
    static final ArchRule repositories_must_be_interfaces_in_repository_package =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().resideInAPackage("..repository..")
                    .andShould().beAssignableTo(JpaRepository.class)
                    .as("Repositories must be interfaces extending JpaRepository in repository package");

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .as("Service layer must not depend on controller layer");
}
