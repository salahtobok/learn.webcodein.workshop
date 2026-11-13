package com.webcodein.workshop.k8s;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.k8s", importOptions = ImportOption.DoNotIncludeTests.class)
class KubernetesReadinessArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_have_rest_controller_annotation =
            classes().that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_have_service_annotation =
            classes().that().resideInAPackage("..service..")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule model_classes_must_be_records =
            classes().that().resideInAPackage("..model..")
                    .should().beRecords();

    @ArchTest
    static final ArchRule controller_names_must_end_with_controller =
            classes().that().resideInAPackage("..controller..")
                    .should().haveSimpleNameEndingWith("Controller");
}
