package com.webcodein.workshop.outbox;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.outbox", importOptions = ImportOption.DoNotIncludeTests.class)
class OutboxArchitectureTest {

    @ArchTest
    static final ArchRule services_should_be_annotated_with_service =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule controllers_should_be_annotated_with_rest_controller =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule repositories_should_be_interfaces_and_extend_jpa_repository =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().beAssignableTo(JpaRepository.class);

    @ArchTest
    static final ArchRule entities_should_be_annotated_with_entity =
            classes()
                    .that().haveSimpleName("Order").or().haveSimpleName("OutboxEvent")
                    .should().beAnnotatedWith(Entity.class);
}
