package com.webcodein.architecture.flashsale;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.architecture.flashsale", importOptions = ImportOption.DoNotIncludeTests.class)
class FlashSaleArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .or().haveSimpleNameEndingWith("Processor")
                    .should().resideInAPackage("..service..")
                    .andShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule entities_must_reside_in_domain_and_be_annotated =
            classes()
                    .that().haveSimpleNameEndingWith("Order")
                    .should().resideInAPackage("..domain..")
                    .andShould().beAnnotatedWith(Entity.class);

    @ArchTest
    static final ArchRule repositories_must_be_interfaces_in_domain =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..domain..")
                    .andShould().beInterfaces();

    @ArchTest
    static final ArchRule no_classes_should_use_legacy_date =
            noClasses()
                    .should().dependOnClassesThat().haveFullyQualifiedName("java.util.Date")
                    .because("Modern Java 25 applications must use modern java.time types");
}
