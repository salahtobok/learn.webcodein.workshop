package com.webcodein.kafka;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.kafka", importOptions = ImportOption.DoNotIncludeTests.class)
public class KafkaArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_be_in_controller_package =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule services_should_be_in_service_package =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..service..");

    @ArchTest
    static final ArchRule controller_classes_should_have_controller_suffix =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule service_classes_should_have_service_suffix =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().haveSimpleNameEndingWith("Service");
}
