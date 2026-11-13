package com.webcodein.workshop.mcp;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop.mcp", importOptions = ImportOption.DoNotIncludeTests.class)
class McpServerArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_be_annotated_with_rest_controller =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_be_annotated_with_service =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule tools_must_be_annotated_with_component =
            classes().that().haveSimpleNameEndingWith("Tools")
                    .should().beAnnotatedWith(Component.class);

    @ArchTest
    static final ArchRule controller_classes_should_reside_in_controller_package =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..");
}
