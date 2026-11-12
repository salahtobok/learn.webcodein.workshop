package com.webcodein.ecommerce;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

@AnalyzeClasses(packages = "com.webcodein.ecommerce", importOptions = ImportOption.DoNotIncludeTests.class)
public class ModulithArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_be_annotated_with_rest_controller =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .because("REST controller endpoints must use @RestController");

    @ArchTest
    static final ArchRule services_must_be_annotated_with_service =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .because("Application services must use @Service");

    @ArchTest
    static final ArchRule repositories_must_be_interfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().beAssignableTo(Repository.class)
                    .because("Spring Data repositories must be interfaces extending Repository");

    @ArchTest
    static final ArchRule no_field_injection =
            NO_CLASSES_SHOULD_USE_FIELD_INJECTION
                    .because("Constructor injection ensures modularity, encapsulation, and testability");

    @ArchTest
    static final ArchRule inventory_must_not_access_order_internals =
            noClasses()
                    .that().resideInAPackage("..inventory..")
                    .should().dependOnClassesThat().resideInAnyPackage("..order.Order", "..order.OrderRepository", "..order.OrderService")
                    .because("Inventory module must only interact with Order module via public events (OrderPlacedEvent)");
}
