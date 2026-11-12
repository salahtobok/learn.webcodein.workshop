package com.webcodein.security.zanzibar;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.security.zanzibar", importOptions = ImportOption.DoNotIncludeTests.class)
class ZanzibarArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_have_controller_suffix_and_annotation =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .as("REST Controllers should have 'Controller' suffix");

    @ArchTest
    static final ArchRule services_must_have_service_suffix_and_annotation =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().haveSimpleNameEndingWith("Service")
                    .as("Service beans should have 'Service' suffix");

    @ArchTest
    static final ArchRule configurations_must_have_config_suffix =
            classes()
                    .that().areAnnotatedWith(Configuration.class)
                    .should().haveSimpleNameEndingWith("Config")
                    .as("Configuration classes should have 'Config' suffix");

    @ArchTest
    static final ArchRule no_standard_output_streams =
            noClasses()
                    .should().accessClassesThat().belongToAnyOf(System.class)
                    .as("Production classes must not access System.out or System.err directly");
}
