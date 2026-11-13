package com.webcodein.workshop.kafka.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.kafka", importOptions = ImportOption.DoNotIncludeTests.class)
public class KafkaArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_reside_in_service_package =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..")
                    .andShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule models_must_reside_in_model_package =
            classes().that().resideInAPackage("..model..")
                    .should().beRecords();
}
