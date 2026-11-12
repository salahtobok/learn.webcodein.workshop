package com.webcodein.workshop.architecture.outbox;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.architecture.outbox", importOptions = ImportOption.DoNotIncludeTests.class)
public class OutboxArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .because("REST controllers must be isolated in the controller package.");

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAnyPackage("..service..", "..publisher..")
                    .because("Business services and publishers must reside in service or publisher packages.");

    @ArchTest
    static final ArchRule entities_must_reside_in_entity_package =
            classes().that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..entity..")
                    .because("JPA persistence entities must reside in the entity package.");

    @ArchTest
    static final ArchRule repositories_must_reside_in_repository_package =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..repository..")
                    .because("Repository interfaces must reside in the repository package.");
}
