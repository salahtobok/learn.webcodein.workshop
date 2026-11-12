package com.webcodein.security.zerotrust.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.security.zerotrust", importOptions = ImportOption.DoNotIncludeTests.class)
public class ZeroTrustArchitectureTest {

    @ArchTest
    public static final ArchRule controllers_must_reside_in_controller_package =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .andShould().haveSimpleNameEndingWith("Controller")
                    .as("REST Controllers should reside in controller package and have 'Controller' suffix");

    @ArchTest
    public static final ArchRule configurations_must_reside_in_config_package =
            classes()
                    .that().areAnnotatedWith(Configuration.class)
                    .should().resideInAPackage("..config..")
                    .andShould().haveSimpleNameEndingWith("Config")
                    .as("Configuration classes should reside in config package and have 'Config' suffix");

    @ArchTest
    public static final ArchRule no_standard_output_streams =
            noClasses()
                    .should().accessClassesThat().belongToAnyOf(System.class)
                    .as("Production classes must not access System.out or System.err directly");
}
